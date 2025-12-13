package co.penny.dronedelivery.orders.repository;

import co.penny.dronedelivery.orders.model.Order;
import co.penny.dronedelivery.orders.model.OrderStatus;
import org.junit.jupiter.api.Test;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class OrderRepositoryTest {

    @Autowired
    OrderRepository repository;

    @Test
    void saveAndFind_shouldWork() {
        UUID id = UUID.randomUUID();
        UUID customerId = UUID.randomUUID();

        Order order = new Order(
                id, customerId,
                1.0, 2.0,
                3.0, 4.0,
                OrderStatus.CREATED,
                null,
                Instant.now()
        );

        repository.save(order);

        Order loaded = repository.findById(id).orElseThrow();
        assertEquals(customerId, loaded.getCustomerId());
        assertEquals(OrderStatus.CREATED, loaded.getStatus());
    }
}
