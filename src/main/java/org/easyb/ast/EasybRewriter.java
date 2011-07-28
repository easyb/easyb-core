package org.easyb.ast;

import org.codehaus.groovy.ast.*;
import org.codehaus.groovy.ast.expr.*;
import org.codehaus.groovy.ast.stmt.*;
import org.codehaus.groovy.control.Janitor;
import org.codehaus.groovy.control.SourceUnit;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class EasybRewriter extends ClassCodeVisitorSupport {

  private final SourceUnit sourceUnit;
  private final Janitor janitor = new Janitor();

  private EasybRewriter(SourceUnit sourceUnit) {
    this.sourceUnit = sourceUnit;
  }

  /**
   * Rewrites all assertions in the given source unit.
   *
   * @param sourceUnit a source unit
   */
  public static void rewrite(SourceUnit sourceUnit) {
    new EasybRewriter(sourceUnit).visitModule();
  }

  private void visitModule() {
    ModuleNode module = sourceUnit.getAST();

    try {
      @SuppressWarnings("unchecked")
      List<ClassNode> classes = module.getClasses();
      for (ClassNode clazz : classes) {
        visitClass(clazz);
      }
    } finally {
      janitor.cleanup();
    }
  }

  @Override
  public void visitMethodCallExpression(MethodCallExpression call) {
    super.visitMethodCallExpression(call);

    if ( easybMethods.contains(call.getMethodAsString()) &&
         call.getObjectExpression() != null && call.getObjectExpression() instanceof VariableExpression && "this".equals(( (VariableExpression) call.getObjectExpression() ).getName())) {
      EasybSourceUnit esu = new EasybSourceUnit(call, sourceUnit, janitor);

      insertNormalizedSource(esu.getNormalizedText(), call);
    }
  }

  private void insertNormalizedSource(String normalizedText, MethodCallExpression call) {
    Expression e = call.getArguments();
    if (e instanceof ArgumentListExpression ) { // don't add to it if there is nothing there
      ArgumentListExpression ale = (ArgumentListExpression)e;
      List<Expression> expressions = new ArrayList<Expression>();
      expressions.add(new ConstantExpression( normalizedText ));
      expressions.add(new ConstantExpression( call.getLineNumber() ));
      expressions.addAll(ale.getExpressions());
      ArgumentListExpression newA = new ArgumentListExpression(expressions);
      call.setArguments(newA);
    }
  }

  private final List<String> easybMethods = Arrays.asList( "it", "scenario", "given", "then", "when", "and", "before", "before_each", "after", "after_each", "examples",
                                                          "where", "but",  "it_behaves_as", "shared_behavior", "shared_behaviour");

  @Override
  protected SourceUnit getSourceUnit() {
    throw new UnsupportedOperationException("getSourceUnit");
  }
}
