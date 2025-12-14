package co.penny.dronedelivery.common.api;

import jakarta.validation.constraints.NotNull;

public record DroneLocationRequest(@NotNull Double lat, @NotNull Double lng) {
}
