package co.penny.dronedelivery.security;

/**
 * Lightweight authenticated user representation stored in the security context.
 */
public record UserPrincipal(String name, Role role, String droneId) {
    public boolean isDrone() {
        return Role.DRONE.equals(role);
    }
}
