package it.pjsoft.reactive.core.preferences;

public class InvalidPreferencesFormatException extends Exception {
    /**
     * Constructs an InvalidPreferencesFormatException with the specified
     * cause.
     *
     * @param  cause the cause (which is saved for later retrieval by the
     *         {@link Throwable#getCause()} method).
     */
    public InvalidPreferencesFormatException(Throwable cause) {
        super(cause);
    }

   /**
    * Constructs an InvalidPreferencesFormatException with the specified
    * detail message.
    *
    * @param   message   the detail message. The detail message is saved for
    *          later retrieval by the {@link Throwable#getMessage()} method.
    */
    public InvalidPreferencesFormatException(String message) {
        super(message);
    }

    /**
     * Constructs an InvalidPreferencesFormatException with the specified
     * detail message and cause.
     *
     * @param  message   the detail message. The detail message is saved for
     *         later retrieval by the {@link Throwable#getMessage()} method.
     * @param  cause the cause (which is saved for later retrieval by the
     *         {@link Throwable#getCause()} method).
     */
    public InvalidPreferencesFormatException(String message, Throwable cause) {
        super(message, cause);
    }

    private static final long serialVersionUID = -791715184232119669L;
}
