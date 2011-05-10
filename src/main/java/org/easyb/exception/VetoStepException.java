package org.easyb.exception;

/**
 * A listener can throw this exception to have a step skipped.
 */
public class VetoStepException extends RuntimeException {

    public VetoStepException() {
    }

    public VetoStepException(String message) {
        super(message);
    }
}
