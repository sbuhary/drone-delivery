package co.penny.dronedelivery.orders.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Order lifecycle statuses.
 */
public enum OrderStatus {
    CREATED("created"),
    RESERVED("reserved"),
    PICKED_UP("picked_up"),
    DELIVERED("delivered"),
    FAILED("failed"),
    WITHDRAWN("withdrawn");

    private final String value;

    OrderStatus(String value) {
        this.value = value;
    }

    @JsonValue
    public String value() {
        return value;
    }

    @JsonCreator
    public static OrderStatus fromValue(String v) {
        for (OrderStatus s : values()) {
            if (s.value.equalsIgnoreCase(v)) return s;
        }
        throw new IllegalArgumentException("Unknown order status: " + v);
    }
}
