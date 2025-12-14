package co.penny.dronedelivery.orders.controller;

import co.penny.dronedelivery.security.JwtAuthenticationFilter;
import co.penny.dronedelivery.common.api.LocationDto;
import co.penny.dronedelivery.security.Role;
import co.penny.dronedelivery.security.UserPrincipal;
import co.penny.dronedelivery.orders.dto.CreateOrderRequest;
import co.penny.dronedelivery.orders.dto.OrderView;
import co.penny.dronedelivery.orders.model.OrderStatus;
import co.penny.dronedelivery.orders.service.OrderService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(
        controllers = EndUserController.class,
        excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = JwtAuthenticationFilter.class)
)
@AutoConfigureMockMvc(addFilters = false)
class EndUserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private OrderService orderService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private TestingAuthenticationToken authToken() {
        return new TestingAuthenticationToken(new UserPrincipal("alice", Role.ENDUSER, null),
                null, "ROLE_ENDUSER");
    }

    @Test
    void createOrder_shouldReturnId() throws Exception {
        UUID orderId = UUID.randomUUID();
        when(orderService.createOrder(any(), any(CreateOrderRequest.class))).thenReturn(orderId);

        CreateOrderRequest request = new CreateOrderRequest(new LocationDto(1.0, 2.0), new LocationDto(3.0, 4.0));

        mockMvc.perform(post("/api/v1/endusers/me/orders")
                        .principal(authToken())
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderId").value(orderId.toString()));
    }

    @Test
    void getOrder_shouldReturnView() throws Exception {
        UUID orderId = UUID.randomUUID();
        OrderView view = new OrderView(OrderStatus.CREATED, null, null, null);
        when(orderService.getOrderForUser(any(), eq(orderId))).thenReturn(view);

        mockMvc.perform(get("/api/v1/endusers/me/orders/{id}", orderId).principal(authToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CREATED"));
    }
}
