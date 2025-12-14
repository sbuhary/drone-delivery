package co.penny.dronedelivery.drones.dto;

import co.penny.dronedelivery.common.api.LocationDto;
import co.penny.dronedelivery.drones.model.DroneStatus;

import java.time.Instant;

public record DroneSummary(String id, DroneStatus status, LocationDto location, Instant lastSeenAt) {
}
