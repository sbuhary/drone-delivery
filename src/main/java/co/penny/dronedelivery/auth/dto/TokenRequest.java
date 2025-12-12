package co.penny.dronedelivery.auth.dto;

import co.penny.dronedelivery.common.enums.UserRole;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * Request payload for the /auth/token endpoint.
 * Carries a caller name and role which will be encoded into the JWT.
 */
public class TokenRequest {

    @NotBlank
    private String name;

    @NotNull
    private UserRole role;

    public TokenRequest() {
    }

    public TokenRequest(String name, UserRole role) {
        this.name = name;
        this.role = role;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }
}
