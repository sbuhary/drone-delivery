package co.penny.dronedelivery.common.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.Map;

/**
 * Simple health/hello controller used to verify that the application
 * is running correctly before adding authentication and business logic.
 */
@RestController
public class HealthController {

    /**
     * Basic health endpoint.
     * @return
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> health() {
        Map<String, Object> body = Map.of(
                "status", "UP",
                "service", "drone-delivery-backend",
                "timestamp", Instant.now().toString()
        );
        return ResponseEntity.ok(body);
    }
}
