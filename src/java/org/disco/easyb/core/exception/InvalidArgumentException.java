package org.disco.easyb.core.exception;

public class InvalidArgumentException extends Exception {
  public InvalidArgumentException() {
  }

  public InvalidArgumentException(String s) {
    super(s);
  }

  public InvalidArgumentException(String s, Throwable throwable) {
    super(s, throwable);
  }

  public InvalidArgumentException(Throwable throwable) {
    super(throwable);
  }
}
