package co.penny.dronedelivery.drones.service;

import co.penny.dronedelivery.common.api.DroneLocationRequest;
import co.penny.dronedelivery.common.exception.BadRequestException;
import co.penny.dronedelivery.common.exception.NotFoundException;
import co.penny.dronedelivery.drones.dto.DroneStatusResponse;
import co.penny.dronedelivery.drones.model.Drone;
import co.penny.dronedelivery.drones.model.DroneStatus;
import co.penny.dronedelivery.drones.repository.DroneRepository;
import co.penny.dronedelivery.jobs.dto.JobResponse;
import co.penny.dronedelivery.jobs.model.Job;
import co.penny.dronedelivery.jobs.model.JobStatus;
import co.penny.dronedelivery.jobs.model.JobType;
import co.penny.dronedelivery.jobs.service.JobService;
import co.penny.dronedelivery.orders.dto.CompletionResult;
import co.penny.dronedelivery.orders.model.Order;
import co.penny.dronedelivery.orders.model.OrderStatus;
import co.penny.dronedelivery.orders.repository.OrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
public class DroneService {

    private final DroneRepository droneRepository;
    private final JobService jobService;
    private final OrderRepository orderRepository;

    public DroneService(DroneRepository droneRepository, JobService jobService, OrderRepository orderRepository) {
        this.droneRepository = droneRepository;
        this.jobService = jobService;
        this.orderRepository = orderRepository;
    }

    /**
     * Updates last seen time and returns current status plus assigned order, if any.
     */
    public DroneStatusResponse getStatus(String droneId) {
        Drone drone = ensureDrone(droneId);
        drone.setLastSeenAt(Instant.now());
        droneRepository.save(drone);
        Optional<Order> currentOrder = orderRepository.findFirstByAssignedDroneId(droneId);
        DroneStatusResponse.CurrentOrder orderView = currentOrder
                .map(o -> new DroneStatusResponse.CurrentOrder(o.getId(), o.getStatus()))
                .orElse(null);
        return new DroneStatusResponse(drone.getStatus(), orderView);
    }

    /**
     * Persists the drone's current coordinates.
     */
    public void updateLocation(String droneId, DroneLocationRequest request) {
        Drone drone = ensureDrone(droneId);
        drone.setLat(request.lat());
        drone.setLng(request.lng());
        droneRepository.save(drone);
    }

    /**
     * Reserves the earliest OPEN job and marks it RESERVED by this drone.
     */
    @Transactional
    public JobResponse reserveJob(String droneId) {
        ensureDrone(droneId);
        Job reserved = jobService.reserveOldestOpenJob(droneId);
        return toJobResponse(reserved);
    }

    /**
     * Promotes a RESERVED job for this drone to IN_PROGRESS and assigns the order.
     */
    @Transactional
    public JobResponse grabOrder(String droneId) {
        ensureDrone(droneId);
        Job job = jobService.getReservedJobForDrone(droneId);

        Order order = orderRepository.findById(job.getOrderId())
                .orElseThrow(() -> new NotFoundException("Order not found for job"));

        order.setAssignedDroneId(droneId);
        order.setStatus(OrderStatus.IN_TRANSIT);
        jobService.markInProgress(job);

        orderRepository.save(order);
        return toJobResponse(job);
    }

    /**
     * Completes the assigned order with the given result and clears assignment.
     */
    @Transactional
    public void completeOrder(String droneId, UUID orderId, CompletionResult result) {
        Order order = orderRepository.findById(orderId)
                .filter(o -> droneId.equals(o.getAssignedDroneId()))
                .orElseThrow(() -> new BadRequestException("Drone not assigned to this order"));
        if (result == CompletionResult.DELIVERED) {
            order.setStatus(OrderStatus.DELIVERED);
        } else {
            order.setStatus(OrderStatus.FAILED);
        }
        order.setAssignedDroneId(null);
        orderRepository.save(order);
    }

    /**
     * Marks drone as BROKEN and, if mid-delivery, creates a HANDOFF job at current location.
     */
    @Transactional
    public void markBroken(String droneId) {
        Drone drone = ensureDrone(droneId);
        drone.setStatus(DroneStatus.BROKEN);
        drone.setLastSeenAt(Instant.now());
        droneRepository.save(drone);

        orderRepository.findFirstByAssignedDroneId(droneId)
                .filter(o -> o.getStatus() == OrderStatus.IN_TRANSIT)
                .ifPresent(order -> {
                    // System creates handoff job at the drone's last known location when it breaks mid-delivery.
                    jobService.createJob(order.getId(), JobType.HANDOFF_PICKUP, drone.getLat(), drone.getLng());

                    order.setStatus(OrderStatus.AWAITING_HANDOFF);
                    order.setAssignedDroneId(null);
                    orderRepository.save(order);
                });
    }

    /**
     * Marks the drone as AVAILABLE (used by drone self-recovery and admin).
     */
    @Transactional
    public Drone markFixed(String droneId) {
        Drone drone = ensureDrone(droneId);
        drone.setStatus(DroneStatus.AVAILABLE);
        drone.setLastSeenAt(Instant.now());
        return droneRepository.save(drone);
    }

    @Transactional
    public Drone adminMarkBroken(String droneId) {
        Drone drone = ensureDrone(droneId);
        drone.setStatus(DroneStatus.BROKEN);
        drone.setLastSeenAt(Instant.now());
        return droneRepository.save(drone);
    }

    @Transactional
    public Drone adminMarkFixed(String droneId) {
        return markFixed(droneId);
    }

    public java.util.List<Drone> getAllDrones() {
        return droneRepository.findAll();
    }

    /**
     * Ensures a drone record exists; creates a default AVAILABLE record if missing.
     */
    private Drone ensureDrone(String droneId) {
        return droneRepository.findById(droneId)
                .orElseGet(() -> {
                    Drone d = new Drone();
                    d.setId(droneId);
                    d.setStatus(DroneStatus.AVAILABLE);
                    d.setLat(0);
                    d.setLng(0);
                    d.setLastSeenAt(Instant.now());
                    return droneRepository.save(d);
                });
    }

    private JobResponse toJobResponse(Job job) {
        return new JobResponse(
                job.getId(),
                job.getOrderId(),
                job.getType(),
                job.getPickupLat(),
                job.getPickupLng(),
                job.getStatus()
        );
    }
}
