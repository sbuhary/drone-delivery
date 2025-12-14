package co.penny.dronedelivery.orders.service;

import co.penny.dronedelivery.common.api.LocationDto;
import co.penny.dronedelivery.common.exception.BadRequestException;
import co.penny.dronedelivery.common.exception.NotFoundException;
import co.penny.dronedelivery.drones.repository.DroneRepository;
import co.penny.dronedelivery.jobs.model.JobType;
import co.penny.dronedelivery.jobs.service.JobService;
import co.penny.dronedelivery.orders.dto.AdminOrderPatchRequest;
import co.penny.dronedelivery.orders.dto.CreateOrderRequest;
import co.penny.dronedelivery.orders.dto.OrderSummary;
import co.penny.dronedelivery.orders.dto.OrderView;
import co.penny.dronedelivery.orders.model.Order;
import co.penny.dronedelivery.orders.model.OrderStatus;
import co.penny.dronedelivery.orders.repository.OrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final JobService jobService;
    private final DroneRepository droneRepository;

    public OrderService(OrderRepository orderRepository, JobService jobService, DroneRepository droneRepository) {
        this.orderRepository = orderRepository;
        this.jobService = jobService;
        this.droneRepository = droneRepository;
    }

    /**
     * Creates a new order for the given user and immediately spawns an OPEN pickup job at the origin.
     */
    @Transactional
    public UUID createOrder(String username, CreateOrderRequest request) {
        Order order = new Order();
        order.setCreatedBy(username);
        order.setOriginLat(request.origin().lat());
        order.setOriginLng(request.origin().lng());
        order.setDestLat(request.destination().lat());
        order.setDestLng(request.destination().lng());
        order.setStatus(OrderStatus.CREATED);
        orderRepository.save(order);

        jobService.createJob(order.getId(), JobType.PICKUP_ORIGIN, order.getOriginLat(), order.getOriginLng());

        return order.getId();
    }

    /**
     * Withdraws a CREATED order and its OPEN job; otherwise fails with bad request.
     */
    @Transactional
    public void withdrawOrder(String username, UUID orderId) {
        Order order = orderRepository.findByIdAndCreatedBy(orderId, username)
                .orElseThrow(() -> new NotFoundException("Order not found"));
        if (order.getStatus() != OrderStatus.CREATED) {
            throw new BadRequestException("Order cannot be withdrawn");
        }
        jobService.deleteOpenJob(order.getId());
        orderRepository.delete(order);
    }

    /**
     * Returns an end-user view of the order; includes drone location if assigned.
     */
    public OrderView getOrderForUser(String username, UUID orderId) {
        Order order = orderRepository.findByIdAndCreatedBy(orderId, username)
                .orElseThrow(() -> new NotFoundException("Order not found"));
        LocationDto locationDto = null;
        if (order.getAssignedDroneId() != null) {
            locationDto = droneRepository.findById(order.getAssignedDroneId())
                    .map(d -> new LocationDto(d.getLat(), d.getLng()))
                    .orElse(null);
        }
        return new OrderView(order.getStatus(), order.getAssignedDroneId(), locationDto, null);
    }

    /**
     * Lists all orders for admin visibility.
     */
    public List<OrderSummary> listOrders() {
        return orderRepository.findAll().stream()
                .map(o -> new OrderSummary(
                        o.getId(),
                        o.getOriginLat(),
                        o.getOriginLng(),
                        o.getDestLat(),
                        o.getDestLng(),
                        o.getStatus(),
                        o.getCreatedBy(),
                        o.getAssignedDroneId(),
                        o.getCreatedAt()
                ))
                .toList();
    }

    /**
     * Applies partial updates to order coordinates.
     */
    @Transactional
    public OrderSummary patchOrder(UUID orderId, AdminOrderPatchRequest patchRequest) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new NotFoundException("Order not found"));

        if (patchRequest.originLat() != null) {
            order.setOriginLat(patchRequest.originLat());
        }
        if (patchRequest.originLng() != null) {
            order.setOriginLng(patchRequest.originLng());
        }
        if (patchRequest.destLat() != null) {
            order.setDestLat(patchRequest.destLat());
        }
        if (patchRequest.destLng() != null) {
            order.setDestLng(patchRequest.destLng());
        }

        Order saved = orderRepository.save(order);
        return new OrderSummary(
                saved.getId(),
                saved.getOriginLat(),
                saved.getOriginLng(),
                saved.getDestLat(),
                saved.getDestLng(),
                saved.getStatus(),
                saved.getCreatedBy(),
                saved.getAssignedDroneId(),
                saved.getCreatedAt()
        );
    }
}
