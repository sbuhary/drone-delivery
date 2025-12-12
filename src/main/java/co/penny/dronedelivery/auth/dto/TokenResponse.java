package co.penny.dronedelivery.auth.dto;

/**
 * Response payload for the /auth/token endpoint.
 * Wraps the generated JWT string.
 */
public class TokenResponse {

    private String token;

    public TokenResponse() {
    }

    public TokenResponse(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
