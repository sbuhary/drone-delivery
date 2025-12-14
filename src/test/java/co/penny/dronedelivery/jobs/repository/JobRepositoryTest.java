package co.penny.dronedelivery.jobs.repository;

import co.penny.dronedelivery.jobs.model.Job;
import co.penny.dronedelivery.jobs.model.JobStatus;
import co.penny.dronedelivery.jobs.model.JobType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class JobRepositoryTest {

    @Autowired
    JobRepository repository;

    @Test
    void findFirstByStatusOrderByCreatedAtAsc_returnsOldestOpenJob() {
        Job newer = buildJob(JobStatus.OPEN, Instant.now());
        Job older = buildJob(JobStatus.OPEN, Instant.now().minusSeconds(60));
        repository.save(newer);
        repository.save(older);

        assertThat(repository.findFirstByStatusOrderByCreatedAtAsc(JobStatus.OPEN))
                .isPresent()
                .get()
                .extracting(Job::getId)
                .isEqualTo(older.getId());
    }

    @Test
    void findByReservedByDroneIdAndStatus_matchesReservedJob() {
        Job job = buildJob(JobStatus.RESERVED, Instant.now());
        job.setReservedByDroneId("drone-1");
        repository.save(job);

        assertThat(repository.findByReservedByDroneIdAndStatus("drone-1", JobStatus.RESERVED))
                .isPresent()
                .get()
                .extracting(Job::getReservedByDroneId)
                .isEqualTo("drone-1");
    }

    private Job buildJob(JobStatus status, Instant createdAt) {
        Job job = new Job();
        job.setId(UUID.randomUUID());
        job.setOrderId(UUID.randomUUID());
        job.setType(JobType.PICKUP_ORIGIN);
        job.setPickupLat(1.0);
        job.setPickupLng(1.0);
        job.setStatus(status);
        job.setCreatedAt(createdAt);
        return job;
    }
}
