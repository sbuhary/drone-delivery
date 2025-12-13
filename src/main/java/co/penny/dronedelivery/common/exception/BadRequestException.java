package co.penny.dronedelivery.common.exception;

/**
 * Thrown when the request is syntactically valid but violates business rules.
 */
public class BadRequestException extends ApiException {
    public BadRequestException(String message) {
        super(message);
    }
}
