package co.penny.dronedelivery.orders.dto;

import co.penny.dronedelivery.orders.model.OrderStatus;

import java.time.Instant;
import java.util.UUID;

/**
 * API response model for returning orders.
 */
public class OrderResponse {

    private UUID id;
    private UUID customerId;
    private LocationDto origin;
    private LocationDto destination;
    private OrderStatus status;
    private UUID assignedDroneId;
    private Instant createdAt;

    public OrderResponse() {
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getCustomerId() {
        return customerId;
    }

    public void setCustomerId(UUID customerId) {
        this.customerId = customerId;
    }

    public LocationDto getOrigin() {
        return origin;
    }

    public void setOrigin(LocationDto origin) {
        this.origin = origin;
    }

    public LocationDto getDestination() {
        return destination;
    }

    public void setDestination(LocationDto destination) {
        this.destination = destination;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public UUID getAssignedDroneId() {
        return assignedDroneId;
    }

    public void setAssignedDroneId(UUID assignedDroneId) {
        this.assignedDroneId = assignedDroneId;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}
