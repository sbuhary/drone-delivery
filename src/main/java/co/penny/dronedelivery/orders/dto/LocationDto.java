package co.penny.dronedelivery.orders.dto;

import jakarta.validation.constraints.NotNull;

/**
 * Represents a latitude/longitude coordinate pair.
 */
public class LocationDto {

    @NotNull
    private Double lat;

    @NotNull
    private Double lng;

    public LocationDto() {
    }

    public LocationDto(Double lat, Double lng) {
        this.lat = lat;
        this.lng = lng;
    }

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getLng() {
        return lng;
    }

    public void setLng(Double lng) {
        this.lng = lng;
    }
}
