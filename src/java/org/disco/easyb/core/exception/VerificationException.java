package org.disco.easyb.core.exception;

/**
 * @author sevensoft
 */
public class VerificationException extends RuntimeException {
    private final Object expected;
    private final Object actual;

    public VerificationException(String message, Object expected, Object actual) {
        super(message);
        this.expected = expected;
        this.actual = actual;
    }

    public VerificationException(String message) {
        this(message, null, null);
    }

    public VerificationException(String message, Exception e) {
        super(message, e);
        expected = null;
        actual = null;
    }

    public Object getActual() {
        return actual;
    }

    public Object getExpected() {
        return expected;
    }

    public String toString() {
        StringBuffer buf = new StringBuffer("VerificationException: ");
        if (getMessage() != null) {
            buf.append(getMessage()).append(": ");
        }
        if (expected != actual) {
            buf.append("expected:[").append(expected).append("] but was:[").append(actual).append("]");
        }
        return buf.toString();
    }
}
