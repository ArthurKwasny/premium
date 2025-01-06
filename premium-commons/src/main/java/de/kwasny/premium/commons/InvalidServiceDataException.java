package de.kwasny.premium.commons;

/**
 * Indicates a validation failure in the service layer. Message should not
 * contain any internal info as it might be redirected as a 4xx HTTP error.
 *
 * @author Arthur Kwasny
 */
public class InvalidServiceDataException extends RuntimeException {

    public InvalidServiceDataException() {
    }

    public InvalidServiceDataException(String message) {
        super(message);
    }

    public InvalidServiceDataException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidServiceDataException(Throwable cause) {
        super(cause);
    }

    public InvalidServiceDataException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}
