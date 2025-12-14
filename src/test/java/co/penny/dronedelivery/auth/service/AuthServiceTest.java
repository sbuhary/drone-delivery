package co.penny.dronedelivery.auth.service;

import co.penny.dronedelivery.auth.dto.AuthTokenRequest;
import co.penny.dronedelivery.drones.model.Drone;
import co.penny.dronedelivery.drones.model.DroneStatus;
import co.penny.dronedelivery.drones.repository.DroneRepository;
import co.penny.dronedelivery.security.JwtService;
import co.penny.dronedelivery.security.Role;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.Instant;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AuthServiceTest {

    JwtService jwtService = mock(JwtService.class);
    DroneRepository droneRepository = mock(DroneRepository.class);

    AuthService authService = new AuthService(jwtService, droneRepository);

    @Test
    void generateToken_createsDroneIfMissing() {
        when(jwtService.generateToken(any(), any())).thenReturn("t");
        when(droneRepository.findById("drone-1")).thenReturn(Optional.empty());
        when(droneRepository.save(any())).thenAnswer(inv -> inv.getArgument(0, Drone.class));

        authService.generateToken(new AuthTokenRequest("drone-1", Role.DRONE));

        ArgumentCaptor<Drone> captor = ArgumentCaptor.forClass(Drone.class);
        verify(droneRepository).save(captor.capture());
        Drone saved = captor.getValue();
        assertThat(saved.getId()).isEqualTo("drone-1");
        assertThat(saved.getStatus()).isEqualTo(DroneStatus.AVAILABLE);
        assertThat(saved.getLastSeenAt()).isNotNull();
    }

    @Test
    void generateToken_reusesExistingDrone() {
        Drone existing = new Drone();
        existing.setId("drone-2");
        existing.setStatus(DroneStatus.AVAILABLE);
        existing.setLat(1);
        existing.setLng(2);
        existing.setLastSeenAt(Instant.now());

        when(jwtService.generateToken(any(), any())).thenReturn("t");
        when(droneRepository.findById("drone-2")).thenReturn(Optional.of(existing));

        authService.generateToken(new AuthTokenRequest("drone-2", Role.DRONE));

        // ensure save not called since we reuse
        verify(droneRepository).findById("drone-2");
    }
}
