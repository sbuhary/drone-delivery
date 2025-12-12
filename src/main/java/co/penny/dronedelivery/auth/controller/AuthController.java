package co.penny.dronedelivery.auth.controller;

import co.penny.dronedelivery.auth.dto.TokenRequest;
import co.penny.dronedelivery.auth.dto.TokenResponse;
import co.penny.dronedelivery.auth.jwt.JwtService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Issues JWT tokens.
 */
@RestController
@RequestMapping("/auth")
public class AuthController {

    private final JwtService jwtService;

    public AuthController(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @PostMapping("/token")
    public ResponseEntity<TokenResponse> token(
            @Valid @RequestBody TokenRequest request
    ) {
        String token =
                jwtService.generateToken(request.getName(), request.getRole());
        return ResponseEntity.ok(new TokenResponse(token));
    }
}
