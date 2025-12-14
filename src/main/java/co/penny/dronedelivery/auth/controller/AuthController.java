package co.penny.dronedelivery.auth.controller;

import co.penny.dronedelivery.auth.dto.AuthTokenRequest;
import co.penny.dronedelivery.auth.dto.TokenResponse;
import co.penny.dronedelivery.auth.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /**
     * Issues a short-lived JWT based on name + role (and droneId when role=DRONE).
     */
    @PostMapping("/token")
    public TokenResponse generateToken(@Valid @RequestBody AuthTokenRequest request) {
        return new TokenResponse(authService.generateToken(request));
    }
}
