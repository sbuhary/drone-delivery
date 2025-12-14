package co.penny.dronedelivery.drones.dto;

import co.penny.dronedelivery.drones.model.DroneStatus;
import co.penny.dronedelivery.orders.model.OrderStatus;

import java.util.UUID;

public record DroneStatusResponse(DroneStatus status, CurrentOrder currentOrder) {

    public record CurrentOrder(UUID orderId, OrderStatus status) {
    }
}
