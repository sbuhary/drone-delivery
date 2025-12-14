package co.penny.dronedelivery.auth.service;

import co.penny.dronedelivery.auth.dto.AuthTokenRequest;
import co.penny.dronedelivery.drones.model.Drone;
import co.penny.dronedelivery.drones.model.DroneStatus;
import co.penny.dronedelivery.drones.repository.DroneRepository;
import co.penny.dronedelivery.security.JwtService;
import co.penny.dronedelivery.security.Role;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@Service
public class AuthService {

    private final JwtService jwtService;
    private final DroneRepository droneRepository;

    public AuthService(JwtService jwtService, DroneRepository droneRepository) {
        this.jwtService = jwtService;
        this.droneRepository = droneRepository;
    }

    /**
     * Generates a JWT containing role (and droneId when applicable).
     * For DRONE tokens, ensures the drone exists (creates if missing).
     */
    public String generateToken(AuthTokenRequest request) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", request.role().name());
        if (Role.DRONE.equals(request.role())) {
            claims.put("droneId", request.name());
            ensureDroneExists(request.name());
        }
        return jwtService.generateToken(request.name(), claims);
    }

    private void ensureDroneExists(String droneId) {
        droneRepository.findById(droneId).orElseGet(() -> {
            Drone drone = new Drone();
            drone.setId(droneId);
            drone.setStatus(DroneStatus.AVAILABLE);
            drone.setLat(0);
            drone.setLng(0);
            drone.setLastSeenAt(Instant.now());
            return droneRepository.save(drone);
        });
    }
}
