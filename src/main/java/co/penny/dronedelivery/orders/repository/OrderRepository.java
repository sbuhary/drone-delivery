package co.penny.dronedelivery.orders.repository;

import co.penny.dronedelivery.orders.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface OrderRepository extends JpaRepository<Order, UUID> {
    Optional<Order> findByIdAndCreatedBy(UUID id, String createdBy);

    Optional<Order> findFirstByAssignedDroneId(String assignedDroneId);
}
