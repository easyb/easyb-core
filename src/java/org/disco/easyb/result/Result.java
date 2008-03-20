package org.disco.easyb.result;

/**
 * Represents the result of verifying an individual behavior.
 *
 * TODO: right now a result has no knowledge of what was the
 * containing behavior-- you can't ask a result for which story it
 * was in, scenario, etc. Error reporting is thus not easy.
 *
 * @author sevensoft [Ken Brooks]
 */
public class Result {
    public static final Type SUCCEEDED = new Type("Succeeded", ".");
    public static final Type FAILED = new Type("Failed", "F");
    public static final Type PENDING = new Type("Pending", "P");

    public static class Type {
        private final String description;
        private final String symbol;

        private Type(String description, String symbol) {
            this.description = description;
            this.symbol = symbol;
        }

        public String toString() {
            return description;
        }

        public String symbol() {
            return symbol;
        }
    }

    private final Type status;
    private final Throwable cause;

    //added to support better error handling
    private String source;

    public Result(Throwable cause) {
        this.cause = cause;
        this.status = getStatusFromCause(cause);
    }

    private static Type getStatusFromCause(Throwable cause) {
        if (cause == null) {
            return SUCCEEDED;
        } else {
            return FAILED;
        }
    }

    public Result(Type status) {
        this.cause = null;
        this.status = status;
    }

    public Type status() {
        return status;
    }

    public Throwable cause() {
        return cause;
    }

    public boolean succeeded() {
        return status == SUCCEEDED;
    }

    public boolean failed() {
        return status == FAILED;
    }

    public boolean pending() {
        return status == PENDING;
    }

    public String toString() {
        return "status: " + status + ", targetException: " + cause;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null) {
            return false;
        }
        if (o.getClass() != getClass()) {
            return false;
        }

        Result other = (Result) o;
        return (status == other.status)
            && (cause == null ? other.cause == null : cause.equals(other.cause));
    }

    /**
     * Override hashCode because we implemented {@link #equals(Object)}
     */
    public int hashCode() {
        int hashCode = 1;
        hashCode = 31 * hashCode + status.hashCode();
        hashCode = 31 * hashCode + (cause == null ? 0 : cause.hashCode());
        return hashCode;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }
}
