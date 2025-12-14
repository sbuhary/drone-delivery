package co.penny.dronedelivery.security;

/**
 * Application roles expressed as JWT claims and Spring authorities.
 */
public enum Role {
    ADMIN,
    ENDUSER,
    DRONE
}
