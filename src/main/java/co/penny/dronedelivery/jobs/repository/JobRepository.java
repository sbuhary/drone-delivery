package co.penny.dronedelivery.jobs.repository;

import co.penny.dronedelivery.jobs.model.Job;
import co.penny.dronedelivery.jobs.model.JobStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface JobRepository extends JpaRepository<Job, UUID> {

    Optional<Job> findFirstByStatusOrderByCreatedAtAsc(JobStatus status);

    Optional<Job> findByOrderIdAndStatus(UUID orderId, JobStatus status);

    Optional<Job> findByReservedByDroneIdAndStatus(String droneId, JobStatus status);

    List<Job> findByOrderId(UUID orderId);
}
