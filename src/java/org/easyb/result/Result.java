package org.easyb.result;

import java.io.Serializable;

/**
 * Represents the result of verifying an individual behavior.
 *
 * @author sevensoft [Ken Brooks]
 */
public class Result implements Serializable {
    public static final Type SUCCEEDED = new Type("success", ".");
    public static final Type FAILED = new Type("failure", "F");
    public static final Type PENDING = new Type("pending", "P");
    public static final Type IGNORED = new Type("ignored", "I");
    public static final Type IN_REVIEW = new Type("in review", "IR");

    public static class Type implements Serializable {
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

        @SuppressWarnings("RedundantIfStatement")
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }

            Type type = (Type) o;

            if (description != null ? !description.equals(type.description) : type.description != null) {
                return false;
            }
            if (symbol != null ? !symbol.equals(type.symbol) : type.symbol != null) {
                return false;
            }

            return true;
        }

        public int hashCode() {
            int result;
            result = (description != null ? description.hashCode() : 0);
            result = 31 * result + (symbol != null ? symbol.hashCode() : 0);
            return result;
        }
    }

    public final Type status;
    public final Throwable cause;

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

    public boolean ignored() {
        return status.equals(IGNORED);
    }

    public boolean succeeded() {
        return status.equals(SUCCEEDED);
    }

    public boolean failed() {
        return status.equals(FAILED);
    }

    public boolean pending() {
        return status.equals(PENDING);
    }

    public boolean inReview() {
        return status.equals(IN_REVIEW);
    }

    public String toString() {
        return "status: " + status + ", targetException: " + cause;
    }

    @SuppressWarnings("RedundantIfStatement")
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Result result = (Result) o;

        if (cause != null ? !cause.equals(result.cause) : result.cause != null) {
            return false;
        }
        if (status != null ? !status.equals(result.status) : result.status != null) {
            return false;
        }

        return true;
    }

    public int hashCode() {
        int result;
        result = (status != null ? status.hashCode() : 0);
        result = 31 * result + (cause != null ? cause.hashCode() : 0);
        return result;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }
}

