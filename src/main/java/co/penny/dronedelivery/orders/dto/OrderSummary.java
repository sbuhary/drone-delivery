package co.penny.dronedelivery.orders.dto;

import co.penny.dronedelivery.orders.model.OrderStatus;

import java.time.Instant;
import java.util.UUID;

public record OrderSummary(UUID id, double originLat, double originLng, double destLat, double destLng,
                           OrderStatus status, String createdBy, String assignedDroneId, Instant createdAt) {
}
