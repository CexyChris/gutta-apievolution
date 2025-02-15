package gutta.apievolution.core.apimodel.provider;

/**
 * This exception is thrown if an error occurs while merging a revision history into a single definition.
 */
public class ModelMergeException extends RuntimeException {

    ModelMergeException(String message) {
        super(message);
    }

    ModelMergeException(String message, Throwable cause) {
        super(message, cause);
    }

}
