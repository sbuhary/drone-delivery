package co.penny.dronedelivery.drones.repository;

import co.penny.dronedelivery.drones.model.Drone;
import co.penny.dronedelivery.drones.model.DroneStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class DroneRepositoryTest {

    @Autowired
    DroneRepository repository;

    @Test
    void saveAndFind_shouldPersistDrone() {
        Drone drone = new Drone();
        drone.setId("drone-x");
        drone.setLat(1.0);
        drone.setLng(2.0);
        drone.setStatus(DroneStatus.AVAILABLE);
        drone.setLastSeenAt(Instant.now());

        repository.save(drone);

        assertThat(repository.findById("drone-x"))
                .isPresent()
                .get()
                .extracting(Drone::getStatus)
                .isEqualTo(DroneStatus.AVAILABLE);
    }
}
