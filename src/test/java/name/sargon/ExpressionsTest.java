package name.sargon;

import name.sargon.ExpressionAnnotations.ConstantExpression;
import name.sargon.ExpressionAnnotations.Expressions;
import name.sargon.ExpressionAnnotations.VariableExpression;

@Expressions
public class ExpressionsTest {

  @ConstantExpression(expected = "42")
  static final String EXPR_1 = "6 * 7";

  @ConstantExpression(expected = "42")
  static final String EXPR_2 = "2 * 3 * 7";

  @VariableExpression(from = "5", to = "10")
  static final String EXPR_3 = "x * x";

}
