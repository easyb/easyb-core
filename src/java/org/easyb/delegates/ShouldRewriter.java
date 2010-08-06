package org.easyb.delegates;

import org.codehaus.groovy.transform.powerassert.*;

import org.codehaus.groovy.ast.*;
import org.codehaus.groovy.ast.expr.*;
import org.codehaus.groovy.ast.stmt.*;
import org.codehaus.groovy.control.Janitor;
import org.codehaus.groovy.control.SourceUnit;
import org.codehaus.groovy.syntax.Token;
import org.codehaus.groovy.syntax.Types;

import org.easyb.exception.VerificationException;

import java.util.List;

/**
 * stolen partly from the AssertRewriter
 */
public class ShouldRewriter extends StatementReplacingVisitorSupport {
  static final VariableExpression recorderVariable = new VariableExpression("$valueRecorder");
  static final Parameter throwVariable = new Parameter(ClassHelper.makeWithoutCaching(VerificationException.class), "$ex");

  private static final ClassNode recorderClass = ClassHelper.makeWithoutCaching(ValueRecorder.class);

  private final SourceUnit sourceUnit;
  private final Janitor janitor = new Janitor();

  private boolean shouldFound;
  private boolean insideClosureExpression;

  private ShouldRewriter(SourceUnit sourceUnit) {
    this.sourceUnit = sourceUnit;
  }

  /**
   * Rewrites all assertions in the given source unit.
   *
   * @param sourceUnit a source unit
   */
  public static void rewrite(SourceUnit sourceUnit) {
    new ShouldRewriter(sourceUnit).visitModule();
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
  }

  @Override
  public void visitExpressionStatement(ExpressionStatement statement) {
    super.visitExpressionStatement(statement);    //To change body of overridden methods use File | Settings | File Templates.
    
    if (insideClosureExpression && statement.getExpression() instanceof MethodCallExpression &&
       ((MethodCallExpression)statement.getExpression()).getMethodAsString().startsWith("should")) {
//      System.out.println("Method is " + statement.getText());
      shouldFound = true;
      rewriteShouldWithTryCatch(statement);
    }
  }

  private void rewriteShouldWithTryCatch(ExpressionStatement call) {

    // hold onto the source text
    ShouldSourceText text;
    try {
      // because source position seems to be more reliable for statements
      // than for expressions, we get the source text for the whole statement
      text = new ShouldSourceText(call, sourceUnit, janitor);
//      System.out.println("source: " + text.getNormalizedText());
    } catch (SourceTextNotAvailableException e) {
      return; // don't rewrite assertions w/o source text
    }

    BlockStatement tryBlock = new BlockStatement();
    tryBlock.addStatement(new ExpressionStatement(call.getExpression()));
    tryBlock.setSourcePosition(call);

    TryCatchStatement tryCatchStat =
      new TryCatchStatement(tryBlock, new BlockStatement());

    BlockStatement catchBlock = new BlockStatement();

    VariableExpression exception = new VariableExpression("$ex");
    catchBlock.addStatement(new ExpressionStatement(new MethodCallExpression(exception, "setSource", new ConstantExpression( text.getNormalizedText() ))));
    catchBlock.addStatement(new ThrowStatement(exception));

    CatchStatement catchStatement = new CatchStatement(throwVariable,
                        catchBlock);
    tryCatchStat.addCatch(catchStatement);

    replaceVisitedStatementWith(tryCatchStat);
  }

  @Override
  public void visitClosureExpression(ClosureExpression expr) {
    boolean old = shouldFound;
    shouldFound = false;

    boolean oldic = insideClosureExpression;

    try {
      insideClosureExpression = true; // should's only occur inside closures
//      System.out.println(expr);
      super.visitClosureExpression(expr);
    } finally {
      insideClosureExpression = oldic;
    }
    if (shouldFound) defineRecorderVariable((BlockStatement) expr.getCode());
    
    shouldFound = old;
  }

  private static void defineRecorderVariable(BlockStatement block) {
    defineRecorderVariable(block.getStatements());
  }

  private static void defineRecorderVariable(List<Statement> stats) {
    // recorder variable needs to be defined in outermost scope,
    // hence we insert it at the beginning of the block
    int insertPos = startsWithConstructorCall(stats) ? 1 : 0;

    stats.add(insertPos,
              new ExpressionStatement(
                new DeclarationExpression(
                  recorderVariable,
                  Token.newSymbol(Types.ASSIGN, -1, -1),
                  new ConstructorCallExpression(
                    recorderClass,
                    ArgumentListExpression.EMPTY_ARGUMENTS
                  )
                )
              )
    );
  }

  private static boolean startsWithConstructorCall(List<Statement> stats) {
    if (stats.size() == 0) return false;
    Statement stat = stats.get(0);
    return stat instanceof ExpressionStatement
           && ( (ExpressionStatement) stat ).getExpression() instanceof ConstructorCallExpression;
  }

  @Override
  protected SourceUnit getSourceUnit() {
    throw new UnsupportedOperationException("getSourceUnit");
  }
}
