package co.penny.dronedelivery.drones.repository;

import co.penny.dronedelivery.drones.model.Drone;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DroneRepository extends JpaRepository<Drone, String> {
}
