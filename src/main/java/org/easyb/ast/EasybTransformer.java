package org.easyb.ast;

import org.codehaus.groovy.GroovyBugError;
import org.codehaus.groovy.ast.ASTNode;
import org.codehaus.groovy.ast.ModuleNode;
import org.codehaus.groovy.control.CompilePhase;
import org.codehaus.groovy.control.SourceUnit;
import org.codehaus.groovy.transform.ASTTransformation;
import org.codehaus.groovy.transform.GroovyASTTransformation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@GroovyASTTransformation(phase = CompilePhase.CANONICALIZATION)
public class EasybTransformer implements ASTTransformation {

  private static final List<String> DEFAULT_ENDS = Arrays.asList("Story.groovy", "Specification.groovy", ".specification", ".story" ); // .shared is used in tests but not mandatory
  private static List<String> suffixes = new ArrayList<String>(DEFAULT_ENDS);

  public EasybTransformer() {
    // allows us to add extra suffixes to the transformer. We should probably also allow pattern matching, but that is perhaps
    // a bit over the top.
    if ( System.getProperty("easyb.ast.suffixes") != null ) {
      String []suf = System.getProperty("easyb.ast.suffixes").toString().split(",");

      for(String s : suf) {
        suffixes.add(s);
      }
    }
  }

  public void visit(ASTNode[] nodes, SourceUnit sourceUnit) {
    if (nodes == null) return;

    if (!( nodes[0] instanceof ModuleNode )) {
      throw new GroovyBugError("tried to apply AssertionTransformation to " + nodes + sourceUnit.getName());
    }


    if (sourceUnit.getName().endsWith("Story.groovy") || sourceUnit.getName().endsWith("Specification.groovy") ||
        sourceUnit.getName().endsWith(".specification") || sourceUnit.getName().endsWith(".story") ||
        sourceUnit.getName().endsWith(".shared")) {

      if ( System.getProperty("easyb.ast.debug") != null )
        System.out.println(String.format("AST processing %s (%s)", ( (ModuleNode) nodes[0] ).getMainClassName(), sourceUnit.getName()) );

      EasybRewriter.rewrite(sourceUnit);
    } else if ( System.getProperty("easyb.ast.debug") != null ) {
      System.out.println("AST processing ignoring " + sourceUnit.getName());
    }
    //AssertionRewriter.rewrite(sourceUnit);
  }
}
