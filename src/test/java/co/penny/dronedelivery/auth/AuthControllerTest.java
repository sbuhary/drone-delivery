package co.penny.dronedelivery.auth;

import co.penny.dronedelivery.auth.controller.AuthController;
import co.penny.dronedelivery.auth.dto.TokenRequest;
import co.penny.dronedelivery.auth.jwt.JwtService;
import co.penny.dronedelivery.common.enums.UserRole;
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

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(
        controllers = AuthController.class,
        excludeFilters = @ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                classes = JwtAuthenticationFilter.class
        )
)
@AutoConfigureMockMvc(addFilters = false) // prevents JwtAuthenticationFilter from being created
class AuthControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    JwtService jwtService;

    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    void createToken_shouldReturnToken() throws Exception {
        when(jwtService.generateToken("test", UserRole.DRONE))
                .thenReturn("fake-token");

        var request = new TokenRequest("test", UserRole.DRONE);

        mockMvc.perform(post("/api/v1/auth/token")
                        .contentType("application/json")
                        .content(mapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("fake-token"));
    }
}
