package co.penny.dronedelivery.admin.controller;

import co.penny.dronedelivery.security.JwtAuthenticationFilter;
import co.penny.dronedelivery.drones.model.DroneStatus;
import co.penny.dronedelivery.drones.service.DroneService;
import co.penny.dronedelivery.orders.dto.AdminOrderPatchRequest;
import co.penny.dronedelivery.orders.dto.OrderSummary;
import co.penny.dronedelivery.orders.model.OrderStatus;
import co.penny.dronedelivery.orders.service.OrderService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(
        controllers = AdminController.class,
        excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = JwtAuthenticationFilter.class)
)
@AutoConfigureMockMvc(addFilters = false)
class AdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private OrderService orderService;
    @MockitoBean
    private DroneService droneService;

    @Test
    void listOrders_shouldReturnOrders() throws Exception {
        when(orderService.listOrders()).thenReturn(List.of(new OrderSummary(
                UUID.randomUUID(), 1, 2, 3, 4,
                OrderStatus.CREATED, "alice", null, Instant.now()
        )));

        mockMvc.perform(get("/api/v1/admin/orders"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].status").value("CREATED"));
    }

    @Test
    void patchOrder_shouldReturnUpdated() throws Exception {
        UUID orderId = UUID.randomUUID();
        when(orderService.patchOrder(any(UUID.class), any(AdminOrderPatchRequest.class)))
                .thenReturn(new OrderSummary(orderId, 1, 2, 3, 4,
                        OrderStatus.CREATED, "alice", null, Instant.now()));

        mockMvc.perform(patch("/api/v1/admin/orders/{id}", orderId)
                        .contentType("application/json")
                        .content("{\"destLat\":9,\"destLng\":9}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(orderId.toString()));
    }

    @Test
    void listDrones_shouldReturnDrones() throws Exception {
        when(droneService.getAllDrones()).thenReturn(List.of(
                new co.penny.dronedelivery.drones.model.Drone() {{
                    setId("d1");
                    setStatus(DroneStatus.AVAILABLE);
                    setLat(1);
                    setLng(2);
                    setLastSeenAt(Instant.now());
                }}
        ));

        mockMvc.perform(get("/api/v1/admin/drones"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("d1"));
    }
}
