package co.penny.dronedelivery.orders.dto;

public record AdminOrderPatchRequest(Double originLat, Double originLng, Double destLat, Double destLng) {
}
