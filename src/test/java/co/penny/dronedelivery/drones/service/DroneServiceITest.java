package co.penny.dronedelivery.drones.service;

import co.penny.dronedelivery.common.api.DroneLocationRequest;
import co.penny.dronedelivery.common.api.LocationDto;
import co.penny.dronedelivery.drones.model.DroneStatus;
import co.penny.dronedelivery.drones.repository.DroneRepository;
import co.penny.dronedelivery.jobs.model.JobStatus;
import co.penny.dronedelivery.jobs.repository.JobRepository;
import co.penny.dronedelivery.orders.dto.CompletionResult;
import co.penny.dronedelivery.orders.dto.CreateOrderRequest;
import co.penny.dronedelivery.orders.model.OrderStatus;
import co.penny.dronedelivery.orders.repository.OrderRepository;
import co.penny.dronedelivery.orders.service.OrderService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class DroneServiceITest {

    @Autowired
    DroneService droneService;
    @Autowired
    OrderService orderService;
    @Autowired
    JobRepository jobRepository;
    @Autowired
    OrderRepository orderRepository;
    @Autowired
    DroneRepository droneRepository;

    @Test
    void reserveGrabCompleteFlow_updatesStates() {
        UUID orderId = orderService.createOrder("alice",
                new CreateOrderRequest(new LocationDto(1.0, 2.0), new LocationDto(3.0, 4.0)));

        var job = droneService.reserveJob("drone-1");
        assertThat(job.status()).isEqualTo(JobStatus.RESERVED);

        var grabbed = droneService.grabOrder("drone-1");
        assertThat(grabbed.status()).isEqualTo(JobStatus.IN_PROGRESS);
        assertThat(orderRepository.findById(orderId)).isPresent()
                .get()
                .extracting(co.penny.dronedelivery.orders.model.Order::getStatus)
                .isEqualTo(OrderStatus.IN_TRANSIT);

        droneService.completeOrder("drone-1", orderId, CompletionResult.DELIVERED);
        assertThat(orderRepository.findById(orderId)).isPresent()
                .get()
                .extracting(co.penny.dronedelivery.orders.model.Order::getStatus)
                .isEqualTo(OrderStatus.DELIVERED);
    }

    @Test
    void markBroken_createsHandoffJob() {
        UUID orderId = orderService.createOrder("alice",
                new CreateOrderRequest(new LocationDto(1.0, 2.0), new LocationDto(3.0, 4.0)));
        droneService.reserveJob("drone-broken");
        droneService.grabOrder("drone-broken");
        droneService.updateLocation("drone-broken", new DroneLocationRequest(9.0, 9.0));

        droneService.markBroken("drone-broken");

        assertThat(droneRepository.findById("drone-broken")).isPresent()
                .get()
                .extracting(d -> d.getStatus())
                .isEqualTo(DroneStatus.BROKEN);
        assertThat(jobRepository.findByOrderId(orderId))
                .anyMatch(j -> j.getStatus() == JobStatus.OPEN && j.getType() == co.penny.dronedelivery.jobs.model.JobType.HANDOFF_PICKUP);
        assertThat(orderRepository.findById(orderId)).isPresent()
                .get()
                .extracting(co.penny.dronedelivery.orders.model.Order::getStatus)
                .isEqualTo(OrderStatus.AWAITING_HANDOFF);
    }
}
