package co.penny.dronedelivery.orders.dto;

import co.penny.dronedelivery.common.api.LocationDto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

public record CreateOrderRequest(@NotNull @Valid LocationDto origin, @NotNull @Valid LocationDto destination) {
}
