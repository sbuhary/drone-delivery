package co.penny.dronedelivery.orders.controller;

import co.penny.dronedelivery.orders.dto.LocationDto;
import co.penny.dronedelivery.orders.dto.CreateOrderRequest;
import co.penny.dronedelivery.orders.dto.OrderResponse;
import co.penny.dronedelivery.orders.model.OrderStatus;
import co.penny.dronedelivery.orders.service.OrderService;
import co.penny.dronedelivery.security.JwtAuthenticationFilter;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(
        controllers = OrderController.class,
        excludeFilters = @ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                classes = JwtAuthenticationFilter.class
        )
)
@AutoConfigureMockMvc(addFilters = false)
class OrderControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    OrderService orderService;

    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    void create_shouldReturn201() throws Exception {
        UUID orderId = UUID.randomUUID();

        OrderResponse response = new OrderResponse();
        response.setId(orderId);
        response.setCustomerId(UUID.randomUUID());
        response.setOrigin(new LocationDto(1.0, 2.0));
        response.setDestination(new LocationDto(3.0, 4.0));
        response.setStatus(OrderStatus.CREATED);
        response.setCreatedAt(Instant.now());

        when(orderService.create(any(), any())).thenReturn(response);

        CreateOrderRequest req = new CreateOrderRequest(
                new LocationDto(1.0, 2.0),
                new LocationDto(3.0, 4.0)
        );

        mockMvc.perform(post("/api/v1/orders")
                        .contentType("application/json")
                        .content(mapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(orderId.toString()))
                .andExpect(jsonPath("$.status").value("created"));
    }
}
