package co.penny.dronedelivery.auth.dto;

import co.penny.dronedelivery.security.Role;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AuthTokenRequest(@NotBlank String name, @NotNull Role role) {
}
