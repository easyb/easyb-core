package org.easyb.ast;

import org.codehaus.groovy.control.SourceUnit;
import org.codehaus.groovy.ast.expr.*;
import org.codehaus.groovy.ast.stmt.*;

public class EasybSourceNotAvailableException extends RuntimeException {
  // only accepts AssertStatementS so that better error messages can be produced
  public EasybSourceNotAvailableException(org.codehaus.groovy.ast.expr.Expression stat, SourceUnit unit, String msg) {
    super(String.format("%s for %s at (%d,%d)-(%d,%d) in %s",
                        msg, stat.getText(), stat.getLineNumber(), stat.getColumnNumber(),
                        stat.getLastLineNumber(), stat.getLastColumnNumber(), unit.getName()));
  }
}
