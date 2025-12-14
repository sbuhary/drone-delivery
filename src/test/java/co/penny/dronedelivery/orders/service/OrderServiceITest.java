package co.penny.dronedelivery.orders.service;

import co.penny.dronedelivery.common.api.LocationDto;
import co.penny.dronedelivery.jobs.repository.JobRepository;
import co.penny.dronedelivery.orders.dto.CreateOrderRequest;
import co.penny.dronedelivery.orders.repository.OrderRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class OrderServiceITest {

    @Autowired
    OrderService orderService;
    @Autowired
    OrderRepository orderRepository;
    @Autowired
    JobRepository jobRepository;

    @Test
    void createOrder_createsOrderAndOpenJob() {
        CreateOrderRequest req = new CreateOrderRequest(new LocationDto(1.0, 2.0), new LocationDto(3.0, 4.0));

        UUID id = orderService.createOrder("alice", req);

        assertThat(orderRepository.findById(id)).isPresent();
        assertThat(jobRepository.findByOrderId(id)).hasSize(1);
    }

    @Test
    void withdrawOrder_removesOrderAndJob() {
        CreateOrderRequest req = new CreateOrderRequest(new LocationDto(1.0, 2.0), new LocationDto(3.0, 4.0));
        UUID id = orderService.createOrder("alice", req);

        orderService.withdrawOrder("alice", id);

        assertThat(orderRepository.findById(id)).isEmpty();
        assertThat(jobRepository.findByOrderId(id)).isEmpty();
    }

    @Test
    void getOrderForUser_returnsLocationWhenAssigned() {
        CreateOrderRequest req = new CreateOrderRequest(new LocationDto(1.0, 2.0), new LocationDto(3.0, 4.0));
        UUID id = orderService.createOrder("alice", req);
        orderRepository.findById(id).ifPresent(o -> {
            o.setAssignedDroneId("drone-1");
            orderRepository.save(o);
        });

        assertThat(orderService.getOrderForUser("alice", id).assignedDroneId()).isEqualTo("drone-1");
    }
}
