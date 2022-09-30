package name.sargon;

import name.sargon.ExpressionAnnotations.Expression;
import name.sargon.ExpressionAnnotations.Expressions;

@Expressions
public class ExpressionsTest {

  @Expression(expected = "42")
  static final String EXPR_1 = "6 * 7";

  @Expression(expected = "43")
  static final String EXPR_3 = "2 * 3 * 7";

}
