package co.penny.dronedelivery.common.exception;

/**
 * Base exception for API errors.
 */
public class ApiException extends RuntimeException {
    public ApiException(String message) {
        super(message);
    }
}
