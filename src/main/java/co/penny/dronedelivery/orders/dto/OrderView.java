package co.penny.dronedelivery.orders.dto;

import co.penny.dronedelivery.common.api.LocationDto;
import co.penny.dronedelivery.orders.model.OrderStatus;

public record OrderView(OrderStatus status, String assignedDroneId, LocationDto currentLocation, Object eta) {
}
