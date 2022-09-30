package name.sargon;

import org.junit.platform.engine.UniqueId;
import org.junit.platform.engine.support.descriptor.AbstractTestDescriptor;

import static name.sargon.ExpressionTestEngine.EXPRESSION_TEST_ENGINE_ID;
import static org.junit.platform.engine.TestDescriptor.Type.CONTAINER_AND_TEST;

class ExpressionTestDescriptor extends AbstractTestDescriptor {

  private final String expression;

  private final String expected;

  ExpressionTestDescriptor(String expression, String expected) {
    super(uniqueIdFor(expression, expected), displayNameFor(expression, expected));

    this.expression = expression;
    this.expected = expected;
  }

  @Override
  public Type getType() {
    return CONTAINER_AND_TEST;
  }

  String getExpression() {
    return expression;
  }

  String getExpected() {
    return expected;
  }

  private static UniqueId uniqueIdFor(String expression, String expected) {
    var uniqueId = UniqueId.forEngine(EXPRESSION_TEST_ENGINE_ID)
            .append("expression", expression);

    if (!expected.isBlank()) {
      uniqueId = uniqueId.append("expected", expected);
    }

    return uniqueId;
  }

  private static String displayNameFor(String expression, String expected) {
    var str = new StringBuilder(expression);

    if (!expected.isBlank()) {
      str.append(" = ").append(expected);
    }

    return str.toString();
  }

}
