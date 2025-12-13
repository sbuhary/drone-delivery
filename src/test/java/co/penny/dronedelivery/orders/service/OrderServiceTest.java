package co.penny.dronedelivery.orders.service;

import co.penny.dronedelivery.common.exception.BadRequestException;
import co.penny.dronedelivery.common.exception.ForbiddenException;
import co.penny.dronedelivery.orders.dto.CreateOrderRequest;
import co.penny.dronedelivery.orders.dto.LocationDto;
import co.penny.dronedelivery.orders.model.OrderStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class OrderServiceTest {

    @Autowired
    OrderService orderService;

    @Test
    void enduser_canCreateAndWithdraw_onlyWhenCreated() {
        var authEndUser = new UsernamePasswordAuthenticationToken(
                "alice", null,
                List.of(new SimpleGrantedAuthority("ROLE_ENDUSER"))
        );

        var created = orderService.create(
                new CreateOrderRequest(
                        new LocationDto(1.0, 2.0),
                        new LocationDto(3.0, 4.0)
                ),
                authEndUser
        );

        assertEquals(OrderStatus.CREATED, created.getStatus());

        // Withdraw succeeds
        assertDoesNotThrow(() ->
                orderService.withdraw(created.getId(), authEndUser)
        );

        // Withdraw again fails
        assertThrows(BadRequestException.class, () ->
                orderService.withdraw(created.getId(), authEndUser)
        );
    }

    @Test
    void drone_cannotCreateOrders() {
        var authDrone = new UsernamePasswordAuthenticationToken(
                "drone-1", null,
                List.of(new SimpleGrantedAuthority("ROLE_DRONE"))
        );

        assertThrows(ForbiddenException.class, () ->
                orderService.create(
                        new CreateOrderRequest(
                                new LocationDto(1.0, 2.0),
                                new LocationDto(3.0, 4.0)
                        ),
                        authDrone
                )
        );
    }
}
