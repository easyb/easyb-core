package org.easyb.delegates;

import org.codehaus.groovy.control.SourceUnit;
import org.codehaus.groovy.ast.expr.*;
import org.codehaus.groovy.ast.stmt.*;

public class ShouldSourceNotAvailableException  extends RuntimeException {
    // only accepts AssertStatementS so that better error messages can be produced
    public ShouldSourceNotAvailableException(ExpressionStatement stat, SourceUnit unit, String msg) {
        super(String.format("%s for %s at (%d,%d)-(%d,%d) in %s",
                msg, stat.getText(), stat.getLineNumber(), stat.getColumnNumber(),
                stat.getLastLineNumber(), stat.getLastColumnNumber(), unit.getName()));
    }
}
