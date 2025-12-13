package co.penny.dronedelivery.common.exception;

/**
 * Thrown when a caller is not allowed to perform an action.
 */
public class ForbiddenException extends ApiException {
    public ForbiddenException(String message) {
        super(message);
    }
}
