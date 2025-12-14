package co.penny.dronedelivery.common.api;

import jakarta.validation.constraints.NotNull;

public record LocationDto(@NotNull Double lat, @NotNull Double lng) {
}
