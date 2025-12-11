package co.penny.dronedelivery.common.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Unit tests for {@link HealthController}.
 * <p>
 * Uses {@link WebMvcTest} to load only the web layer and
 * validate the behavior of the /health endpoint.
 */
@WebMvcTest(controllers = HealthController.class)
class HealthControllerTest {

    private final MockMvc mockMvc;

    /**
     * Constructs a new instance of {@link HealthControllerTest}.
     *
     * @param mockMvc MockMvc instance injected by Spring Test
     */
    @Autowired
    HealthControllerTest(MockMvc mockMvc) {
        this.mockMvc = mockMvc;
    }

    /**
     * Verifies that the /health endpoint returns HTTP 200
     * with a JSON body containing the expected fields.
     *
     * @throws Exception if the MVC call fails
     */
    @Test
    @DisplayName("GET /health should return UP status")
    void health_shouldReturnUpStatus() throws Exception {
        mockMvc.perform(get("/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("UP")))
                .andExpect(jsonPath("$.service", is("drone-delivery-backend")))
                .andExpect(jsonPath("$.timestamp", notNullValue()));
    }
}
