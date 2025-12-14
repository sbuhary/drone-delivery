package co.penny.dronedelivery.orders.controller;

import co.penny.dronedelivery.security.UserPrincipal;
import co.penny.dronedelivery.orders.dto.CreateOrderRequest;
import co.penny.dronedelivery.orders.dto.CreateOrderResponse;
import co.penny.dronedelivery.orders.dto.OrderView;
import co.penny.dronedelivery.orders.service.OrderService;
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
@RequestMapping("/api/v1/endusers/me")
@PreAuthorize("hasRole('ENDUSER')")
public class EndUserController {

    private final OrderService orderService;

    public EndUserController(OrderService orderService) {
        this.orderService = orderService;
    }

    /**
     * Allows an end user to submit a new order, which creates both the order and its initial pickup job.
     */
    @PostMapping("/orders")
    public CreateOrderResponse createOrder(@AuthenticationPrincipal UserPrincipal principal,
                                           @Valid @RequestBody CreateOrderRequest request) {
        UUID orderId = orderService.createOrder(principal.name(), request);
        return new CreateOrderResponse(orderId);
    }

    /**
     * Withdraws an order only while it is still in CREATED state and has an OPEN pickup job.
     */
    @PostMapping("/orders/{orderId}/withdraw")
    public ResponseEntity<Void> withdrawOrder(@AuthenticationPrincipal UserPrincipal principal,
                                              @PathVariable UUID orderId) {
        orderService.withdrawOrder(principal.name(), orderId);
        return ResponseEntity.noContent().build();
    }

    /**
     * Returns current order view for the authenticated end user (includes assigned drone location if any).
     */
    @GetMapping("/orders/{orderId}")
    public OrderView getOrder(@AuthenticationPrincipal UserPrincipal principal,
                              @PathVariable UUID orderId) {
        return orderService.getOrderForUser(principal.name(), orderId);
    }
}
