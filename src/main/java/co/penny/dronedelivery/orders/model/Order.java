package co.penny.dronedelivery.orders.model;

import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

/**
 * Persistent Order entity stored in DB (via JPA).
 */
@Entity
@Table(name = "orders")
public class Order {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "customer_id", nullable = false, updatable = false)
    private UUID customerId;

    @Column(name = "origin_lat", nullable = false)
    private double originLat;

    @Column(name = "origin_lng", nullable = false)
    private double originLng;

    @Column(name = "destination_lat", nullable = false)
    private double destinationLat;

    @Column(name = "destination_lng", nullable = false)
    private double destinationLng;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private OrderStatus status;

    @Column(name = "assigned_drone_id")
    private UUID assignedDroneId;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    /**
     * Optimistic locking for safe concurrent updates (recommended).
     */
    @Version
    @Column(name = "version", nullable = false)
    private long version;

    protected Order() {
        // for JPA
    }

    public Order(UUID id, UUID customerId,
                 double originLat, double originLng,
                 double destinationLat, double destinationLng,
                 OrderStatus status, UUID assignedDroneId, Instant createdAt) {
        this.id = id;
        this.customerId = customerId;
        this.originLat = originLat;
        this.originLng = originLng;
        this.destinationLat = destinationLat;
        this.destinationLng = destinationLng;
        this.status = status;
        this.assignedDroneId = assignedDroneId;
        this.createdAt = createdAt;
    }

    public UUID getId() {
        return id;
    }

    public UUID getCustomerId() {
        return customerId;
    }

    public double getOriginLat() {
        return originLat;
    }

    public double getOriginLng() {
        return originLng;
    }

    public double getDestinationLat() {
        return destinationLat;
    }

    public double getDestinationLng() {
        return destinationLng;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public UUID getAssignedDroneId() {
        return assignedDroneId;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public long getVersion() {
        return version;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public void setAssignedDroneId(UUID assignedDroneId) {
        this.assignedDroneId = assignedDroneId;
    }
}
