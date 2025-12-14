package co.penny.dronedelivery.admin.controller;

import co.penny.dronedelivery.drones.dto.DroneSummary;
import co.penny.dronedelivery.common.api.LocationDto;
import co.penny.dronedelivery.drones.model.Drone;
import co.penny.dronedelivery.drones.service.DroneService;
import co.penny.dronedelivery.orders.dto.AdminOrderPatchRequest;
import co.penny.dronedelivery.orders.dto.OrderSummary;
import co.penny.dronedelivery.orders.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final OrderService orderService;
    private final DroneService droneService;

    public AdminController(OrderService orderService, DroneService droneService) {
        this.orderService = orderService;
        this.droneService = droneService;
    }

    /**
     * Lists all orders for administrative visibility.
     */
    @GetMapping("/orders")
    public List<OrderSummary> listOrders() {
        return orderService.listOrders();
    }

    /**
     * Partially updates order coordinates.
     */
    @PatchMapping("/orders/{orderId}")
    public OrderSummary patchOrder(@PathVariable UUID orderId,
                                   @Valid @RequestBody AdminOrderPatchRequest request) {
        return orderService.patchOrder(orderId, request);
    }

    /**
     * Lists all drones and their last known status/location.
     */
    @GetMapping("/drones")
    public List<DroneSummary> listDrones() {
        return droneService.getAllDrones().stream()
                .map(d -> new DroneSummary(d.getId(), d.getStatus(),
                        new LocationDto(d.getLat(), d.getLng()), d.getLastSeenAt()))
                .toList();
    }

    /**
     * Marks a drone as BROKEN without creating handoff jobs (admin-only).
     */
    @PostMapping("/drones/{droneId}/broken")
    public DroneSummary markDroneBroken(@PathVariable String droneId) {
        Drone drone = droneService.adminMarkBroken(droneId);
        return new DroneSummary(drone.getId(), drone.getStatus(),
                new LocationDto(drone.getLat(), drone.getLng()), drone.getLastSeenAt());
    }

    /**
     * Marks a drone as AVAILABLE (admin-only).
     */
    @PostMapping("/drones/{droneId}/fixed")
    public DroneSummary markDroneFixed(@PathVariable String droneId) {
        Drone drone = droneService.adminMarkFixed(droneId);
        return new DroneSummary(drone.getId(), drone.getStatus(),
                new LocationDto(drone.getLat(), drone.getLng()), drone.getLastSeenAt());
    }
}
