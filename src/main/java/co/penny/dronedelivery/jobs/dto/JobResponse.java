package co.penny.dronedelivery.jobs.dto;

import co.penny.dronedelivery.jobs.model.JobStatus;
import co.penny.dronedelivery.jobs.model.JobType;

import java.util.UUID;

public record JobResponse(UUID id, UUID orderId, JobType type, double pickupLat, double pickupLng, JobStatus status) {
}
