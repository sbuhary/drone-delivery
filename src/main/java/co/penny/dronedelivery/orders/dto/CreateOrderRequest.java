package co.penny.dronedelivery.orders.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

/**
 * Request to create a new order.
 */
public class CreateOrderRequest {

    @Valid
    @NotNull
    private LocationDto origin;

    @Valid
    @NotNull
    private LocationDto destination;

    public CreateOrderRequest() {
    }

    public CreateOrderRequest(LocationDto origin, LocationDto destination) {
        this.origin = origin;
        this.destination = destination;
    }

    public LocationDto getOrigin() {
        return origin;
    }

    public void setOrigin(LocationDto origin) {
        this.origin = origin;
    }

    public LocationDto getDestination() {
        return destination;
    }

    public void setDestination(LocationDto destination) {
        this.destination = destination;
    }
}
