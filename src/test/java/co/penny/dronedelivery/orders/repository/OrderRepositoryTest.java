package co.penny.dronedelivery.orders.repository;

import co.penny.dronedelivery.orders.model.Order;
import co.penny.dronedelivery.orders.model.OrderStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class OrderRepositoryTest {

    @Autowired
    OrderRepository repository;

    @Test
    void saveAndFind_shouldPersistOrder() {
        Order order = new Order();
        order.setId(UUID.randomUUID());
        order.setCreatedBy("alice");
        order.setOriginLat(1.0);
        order.setOriginLng(2.0);
        order.setDestLat(3.0);
        order.setDestLng(4.0);
        order.setStatus(OrderStatus.CREATED);
        order.setCreatedAt(Instant.now());

        repository.save(order);

        Order loaded = repository.findByIdAndCreatedBy(order.getId(), "alice").orElseThrow();
        assertThat(loaded.getStatus()).isEqualTo(OrderStatus.CREATED);
        assertThat(loaded.getOriginLat()).isEqualTo(1.0);
    }

    @Test
    void findFirstByAssignedDroneId_shouldReturnOrderForDrone() {
        Order order = new Order();
        order.setCreatedBy("bob");
        order.setOriginLat(1.0);
        order.setOriginLng(1.0);
        order.setDestLat(2.0);
        order.setDestLng(2.0);
        order.setAssignedDroneId("drone-1");
        order.setStatus(OrderStatus.IN_TRANSIT);
        repository.save(order);

        assertThat(repository.findFirstByAssignedDroneId("drone-1"))
                .isPresent()
                .get()
                .extracting(Order::getStatus)
                .isEqualTo(OrderStatus.IN_TRANSIT);
    }
}
