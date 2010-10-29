package org.easyb.exception;

/**
 * @author sevensoft
 */
public class VerificationException extends RuntimeException {
    private final Object expected;
    private final Object actual;
    private String source;

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

  public String getSource() {
    return source;
  }

  public void setSource(String source) {
    System.out.println("source set to " + source);
    this.source = source;
  }


    public String toString() {
        StringBuffer buf = new StringBuffer("VerificationException: ");

      if (source != null)
        buf.append(source).append("\n");
      else {
        System.out.println("source is null");

      }
      
        if (getMessage() != null) {
            buf.append(getMessage()).append(": ");
        }
        if (expected != actual) {
            buf.append("expected:[").append(expected).append("] but was:[").append(actual).append("]");
        }
        return buf.toString();
    }
}
