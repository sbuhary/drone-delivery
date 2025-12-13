package co.penny.dronedelivery.orders.service;

import co.penny.dronedelivery.common.exception.BadRequestException;
import co.penny.dronedelivery.common.exception.ForbiddenException;
import co.penny.dronedelivery.common.exception.NotFoundException;
import co.penny.dronedelivery.orders.dto.CreateOrderRequest;
import co.penny.dronedelivery.orders.dto.LocationDto;
import co.penny.dronedelivery.orders.dto.OrderResponse;
import co.penny.dronedelivery.orders.model.Order;
import co.penny.dronedelivery.orders.model.OrderStatus;
import co.penny.dronedelivery.orders.repository.OrderRepository;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * Core business logic for Orders (JPA-backed).
 */
@Service
public class OrderService {

    private final OrderRepository repository;

    public OrderService(OrderRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public OrderResponse create(CreateOrderRequest request, Authentication auth) {
        requireRole(auth, "ROLE_ADMIN", "ROLE_ENDUSER");

        UUID customerId = customerIdFromCaller(auth.getName());

        Order order = new Order(
                UUID.randomUUID(),
                customerId,
                request.getOrigin().getLat(),
                request.getOrigin().getLng(),
                request.getDestination().getLat(),
                request.getDestination().getLng(),
                OrderStatus.CREATED,
                null,
                Instant.now()
        );

        Order saved = repository.save(order);
        return toResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<OrderResponse> list(Authentication auth) {
        requireRole(auth, "ROLE_ADMIN", "ROLE_ENDUSER");

        boolean isAdmin = hasAuthority(auth, "ROLE_ADMIN");
        List<Order> orders = isAdmin
                ? repository.findAll()
                : repository.findByCustomerId(customerIdFromCaller(auth.getName()));

        return orders.stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public OrderResponse get(UUID orderId, Authentication auth) {
        requireRole(auth, "ROLE_ADMIN", "ROLE_ENDUSER");

        Order order = repository.findById(orderId)
                .orElseThrow(() -> new NotFoundException("Order not found: " + orderId));

        if (hasAuthority(auth, "ROLE_ADMIN")) return toResponse(order);

        UUID customerId = customerIdFromCaller(auth.getName());
        if (!order.getCustomerId().equals(customerId)) {
            throw new ForbiddenException("You are not allowed to access this order");
        }

        return toResponse(order);
    }

    @Transactional
    public void withdraw(UUID orderId, Authentication auth) {
        requireRole(auth, "ROLE_ENDUSER");

        Order order = repository.findById(orderId)
                .orElseThrow(() -> new NotFoundException("Order not found: " + orderId));

        UUID customerId = customerIdFromCaller(auth.getName());
        if (!order.getCustomerId().equals(customerId)) {
            throw new ForbiddenException("You are not allowed to withdraw this order");
        }

        if (order.getStatus() != OrderStatus.CREATED) {
            throw new BadRequestException("Order cannot be withdrawn in current state");
        }

        order.setStatus(OrderStatus.WITHDRAWN);
        repository.save(order);
    }

    private OrderResponse toResponse(Order order) {
        OrderResponse r = new OrderResponse();
        r.setId(order.getId());
        r.setCustomerId(order.getCustomerId());
        r.setOrigin(new LocationDto(order.getOriginLat(), order.getOriginLng()));
        r.setDestination(new LocationDto(order.getDestinationLat(), order.getDestinationLng()));
        r.setStatus(order.getStatus());
        r.setAssignedDroneId(order.getAssignedDroneId());
        r.setCreatedAt(order.getCreatedAt());
        return r;
    }

    private void requireRole(Authentication auth, String... allowedAuthorities) {
        for (String a : allowedAuthorities) {
            if (hasAuthority(auth, a)) return;
        }
        throw new ForbiddenException("Insufficient privileges");
    }

    private boolean hasAuthority(Authentication auth, String authority) {
        return auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals(authority));
    }

    private UUID customerIdFromCaller(String username) {
        return UUID.nameUUIDFromBytes(("user:" + username).getBytes(StandardCharsets.UTF_8));
    }
}
