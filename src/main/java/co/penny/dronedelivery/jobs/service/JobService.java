package co.penny.dronedelivery.jobs.service;

import co.penny.dronedelivery.common.exception.BadRequestException;
import co.penny.dronedelivery.jobs.model.Job;
import co.penny.dronedelivery.jobs.model.JobStatus;
import co.penny.dronedelivery.jobs.model.JobType;
import co.penny.dronedelivery.jobs.repository.JobRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
public class JobService {

    private final JobRepository jobRepository;

    public JobService(JobRepository jobRepository) {
        this.jobRepository = jobRepository;
    }

    /**
     * Creates a new OPEN job for the given order.
     */
    @Transactional
    public Job createJob(UUID orderId, JobType type, double pickupLat, double pickupLng) {
        Job job = new Job();
        job.setOrderId(orderId);
        job.setType(type);
        job.setPickupLat(pickupLat);
        job.setPickupLng(pickupLng);
        job.setStatus(JobStatus.OPEN);
        job.setCreatedAt(Instant.now());
        return jobRepository.save(job);
    }

    /**
     * Reserves the earliest OPEN job for a drone.
     */
    @Transactional
    public Job reserveOldestOpenJob(String droneId) {
        Job job = jobRepository.findFirstByStatusOrderByCreatedAtAsc(JobStatus.OPEN)
                .orElseThrow(() -> new BadRequestException("No open jobs"));
        job.setStatus(JobStatus.RESERVED);
        job.setReservedByDroneId(droneId);
        return jobRepository.save(job);
    }

    public Job getReservedJobForDrone(String droneId) {
        return jobRepository.findByReservedByDroneIdAndStatus(droneId, JobStatus.RESERVED)
                .orElseThrow(() -> new BadRequestException("No reserved job for this drone"));
    }

    @Transactional
    public Job markInProgress(Job job) {
        job.setStatus(JobStatus.IN_PROGRESS);
        return jobRepository.save(job);
    }

    @Transactional
    public void deleteOpenJob(UUID orderId) {
        jobRepository.findByOrderIdAndStatus(orderId, JobStatus.OPEN)
                .ifPresentOrElse(jobRepository::delete,
                        () -> { throw new BadRequestException("Order has no open job to withdraw"); });
    }

    public List<Job> findByOrderId(UUID orderId) {
        return jobRepository.findByOrderId(orderId);
    }
}
