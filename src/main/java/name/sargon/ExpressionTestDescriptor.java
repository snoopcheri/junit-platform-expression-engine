package name.sargon;

import org.junit.platform.engine.UniqueId;
import org.junit.platform.engine.support.descriptor.AbstractTestDescriptor;

import javax.annotation.Nullable;

import static name.sargon.ExpressionTestEngine.EXPRESSION_TEST_ENGINE_ID;
import static org.junit.platform.engine.TestDescriptor.Type.CONTAINER_AND_TEST;

class ExpressionTestDescriptor extends AbstractTestDescriptor {

  private final String expression;

  private final String expected;

  ExpressionTestDescriptor(String expression, String expected) {
    this(expression, expected, null);
  }

  ExpressionTestDescriptor(String expression, String expected, @Nullable Integer xValue) {
    super(uniqueIdFor(expression, expected, xValue), displayNameFor(expression, expected, xValue));

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

  private static UniqueId uniqueIdFor(String expression, String expected, @Nullable Integer xValue) {
    var uniqueId = UniqueId.forEngine(EXPRESSION_TEST_ENGINE_ID)
            .append("expression", expression);

    if (!expected.isBlank()) {
      uniqueId = uniqueId.append("expected", expected);
    }

    if (xValue != null) {
      uniqueId = uniqueId.append("xValue", String.valueOf(xValue));
    }

    return uniqueId;
  }

  private static String displayNameFor(String expression, String expected, @Nullable Integer xValue) {
    var str = new StringBuilder(expression);

    if (!expected.isBlank()) {
      str.append(" = ").append(expected);
    }

    if (xValue != null) {
      str.append(" [for x=").append(xValue).append("]");
    }

    return str.toString();
  }

}
