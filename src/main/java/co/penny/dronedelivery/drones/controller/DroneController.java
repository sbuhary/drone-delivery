package co.penny.dronedelivery.drones.controller;

import co.penny.dronedelivery.security.UserPrincipal;
import co.penny.dronedelivery.common.api.DroneLocationRequest;
import co.penny.dronedelivery.drones.dto.DroneStatusResponse;
import co.penny.dronedelivery.drones.service.DroneService;
import co.penny.dronedelivery.jobs.dto.JobResponse;
import co.penny.dronedelivery.orders.dto.CompleteOrderRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/drones/me")
@PreAuthorize("hasRole('DRONE')")
public class DroneController {

    private final DroneService droneService;

    public DroneController(DroneService droneService) {
        this.droneService = droneService;
    }

    /**
     * Refreshes lastSeenAt and reports the drone's current order (if any).
     */
    @GetMapping("/status")
    public DroneStatusResponse getStatus(@AuthenticationPrincipal UserPrincipal principal) {
        String droneId = principal.droneId();
        return droneService.getStatus(droneId);
    }

    /**
     * Persist current coordinates for this drone.
     */
    @PostMapping("/location")
    public ResponseEntity<Void> updateLocation(@AuthenticationPrincipal UserPrincipal principal,
                                               @Valid @RequestBody DroneLocationRequest request) {
        droneService.updateLocation(principal.droneId(), request);
        return ResponseEntity.noContent().build();
    }

    /**
     * Reserves the earliest OPEN job for this drone.
     */
    @PostMapping("/jobs/reserve")
    public JobResponse reserveJob(@AuthenticationPrincipal UserPrincipal principal) {
        return droneService.reserveJob(principal.droneId());
    }

    /**
     * Claims the reserved job and moves its order to IN_TRANSIT.
     */
    @PostMapping("/orders/grab")
    public JobResponse grabOrder(@AuthenticationPrincipal UserPrincipal principal) {
        return droneService.grabOrder(principal.droneId());
    }

    /**
     * Marks the assigned order as delivered/failed and clears assignment.
     */
    @PostMapping("/orders/{orderId}/complete")
    public ResponseEntity<Void> completeOrder(@AuthenticationPrincipal UserPrincipal principal,
                                              @PathVariable UUID orderId,
                                              @Valid @RequestBody CompleteOrderRequest request) {
        droneService.completeOrder(principal.droneId(), orderId, request.result());
        return ResponseEntity.noContent().build();
    }

    /**
     * Flags the drone as BROKEN and triggers handoff job if carrying an order.
     */
    @PostMapping("/broken")
    public ResponseEntity<Void> markBroken(@AuthenticationPrincipal UserPrincipal principal) {
        droneService.markBroken(principal.droneId());
        return ResponseEntity.noContent().build();
    }
}
