package co.penny.dronedelivery.common.exception;

/**
 * Thrown when a resource is not found.
 */
public class NotFoundException extends ApiException {
    public NotFoundException(String message) {
        super(message);
    }
}
