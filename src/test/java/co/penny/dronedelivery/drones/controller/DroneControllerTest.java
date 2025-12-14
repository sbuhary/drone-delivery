package co.penny.dronedelivery.drones.controller;

import co.penny.dronedelivery.security.JwtAuthenticationFilter;
import co.penny.dronedelivery.common.api.DroneLocationRequest;
import co.penny.dronedelivery.security.Role;
import co.penny.dronedelivery.security.UserPrincipal;
import co.penny.dronedelivery.drones.dto.DroneStatusResponse;
import co.penny.dronedelivery.drones.service.DroneService;
import co.penny.dronedelivery.jobs.dto.JobResponse;
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

import static co.penny.dronedelivery.drones.model.DroneStatus.AVAILABLE;
import static co.penny.dronedelivery.jobs.model.JobStatus.RESERVED;
import static co.penny.dronedelivery.jobs.model.JobType.PICKUP_ORIGIN;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(
        controllers = DroneController.class,
        excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = JwtAuthenticationFilter.class)
)
@AutoConfigureMockMvc(addFilters = false)
class DroneControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private DroneService droneService;

    private final ObjectMapper mapper = new ObjectMapper();

    private TestingAuthenticationToken auth() {
        return new TestingAuthenticationToken(new UserPrincipal("drone-1", Role.DRONE, "drone-1"),
                null, "ROLE_DRONE");
    }

    @Test
    void getStatus_shouldReturnStatus() throws Exception {
        when(droneService.getStatus(org.mockito.ArgumentMatchers.anyString()))
                .thenReturn(new DroneStatusResponse(AVAILABLE, null));

        mockMvc.perform(get("/api/v1/drones/me/status")
                        .with(authentication(auth()))
                        .accept("application/json"))
                .andExpect(status().isOk());
    }

    @Test
    void reserveJob_shouldReturnJob() throws Exception {
        UUID jobId = UUID.randomUUID();
        when(droneService.reserveJob(org.mockito.ArgumentMatchers.anyString()))
                .thenReturn(new JobResponse(jobId, UUID.randomUUID(), PICKUP_ORIGIN, 1, 2, RESERVED));

        mockMvc.perform(post("/api/v1/drones/me/jobs/reserve")
                        .with(authentication(auth()))
                        .accept("application/json"))
                .andExpect(status().isOk());
    }

    @Test
    void updateLocation_shouldReturnNoContent() throws Exception {
        mockMvc.perform(post("/api/v1/drones/me/location")
                        .with(authentication(auth()))
                        .contentType("application/json")
                        .content(mapper.writeValueAsString(new DroneLocationRequest(1.0, 2.0))))
                .andExpect(status().isNoContent());
    }
}
