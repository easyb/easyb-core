/*
  * thrown when the easyb parser encounters grammar in a location it should not be
 */
package org.easyb;


public class IncorrectGrammarException extends RuntimeException {
  public IncorrectGrammarException(String msg) {
    super(msg)
  }
}