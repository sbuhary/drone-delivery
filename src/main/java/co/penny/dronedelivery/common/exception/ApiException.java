package co.penny.dronedelivery.common.exception;

/**
 * Base unchecked exception for API-layer errors that should map to HTTP responses.
 */
public abstract class ApiException extends RuntimeException {
    public ApiException(String message) {
        super(message);
    }
}
