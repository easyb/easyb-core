package org.easyb.delegates;

import org.codehaus.groovy.GroovyBugError;
import org.codehaus.groovy.ast.ASTNode;
import org.codehaus.groovy.ast.ModuleNode;
import org.codehaus.groovy.control.CompilePhase;
import org.codehaus.groovy.control.SourceUnit;
import org.codehaus.groovy.transform.ASTTransformation;
import org.codehaus.groovy.transform.GroovyASTTransformation;

@GroovyASTTransformation(phase = CompilePhase.CANONICALIZATION)
public class ShouldTransformation implements ASTTransformation {

  public void visit(ASTNode[] nodes, SourceUnit sourceUnit) {
    if (nodes == null) return;


    if (!( nodes[0] instanceof ModuleNode )) {
      throw new GroovyBugError("tried to apply AssertionTransformation to " + nodes + sourceUnit.getName());
    }

    if (sourceUnit.getName().endsWith("Story.groovy") || sourceUnit.getName().endsWith("Specification.groovy") ||
        sourceUnit.getName().endsWith(".specification") || sourceUnit.getName().endsWith(".story")) {

      System.out.println("visit called " + ( (ModuleNode) nodes[0] ).getMainClassName());
      ShouldRewriter.rewrite(sourceUnit);
    } else {
      System.out.println("Ignoroing " + sourceUnit.getName());
    }
    //AssertionRewriter.rewrite(sourceUnit);
  }
}
