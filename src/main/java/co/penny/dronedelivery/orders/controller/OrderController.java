package co.penny.dronedelivery.orders.controller;

import co.penny.dronedelivery.orders.dto.CreateOrderRequest;
import co.penny.dronedelivery.orders.dto.OrderResponse;
import co.penny.dronedelivery.orders.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.UUID;

/**
 * REST API for order operations.
 * Paths are relative to the context path (/api/v1).
 */
@RestController
@RequestMapping("/api/v1/orders")
public class OrderController {

    private final OrderService service;

    public OrderController(OrderService service) {
        this.service = service;
    }

    /**
     * Creates a new order.
     */
    @PostMapping
    public ResponseEntity<OrderResponse> create(@Valid @RequestBody CreateOrderRequest request,
                                                Authentication authentication) {
        OrderResponse created = service.create(request, authentication);
        return ResponseEntity.created(URI.create("/api/v1/orders/" + created.getId())).body(created);
    }

    /**
     * Lists orders (admin = all, enduser = own).
     */
    @GetMapping
    public ResponseEntity<List<OrderResponse>> list(Authentication authentication) {
        return ResponseEntity.ok(service.list(authentication));
    }

    /**
     * Gets a single order by ID (admin = any, enduser = own).
     */
    @GetMapping("/{orderId}")
    public ResponseEntity<OrderResponse> get(@PathVariable UUID orderId, Authentication authentication) {
        return ResponseEntity.ok(service.get(orderId, authentication));
    }

    /**
     * Withdraws an order (enduser only, only in created state).
     */
    @DeleteMapping("/{orderId}")
    public ResponseEntity<Void> withdraw(@PathVariable UUID orderId, Authentication authentication) {
        service.withdraw(orderId, authentication);
        return ResponseEntity.noContent().build();
    }
}
