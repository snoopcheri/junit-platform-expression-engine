package name.sargon;

import org.junit.platform.engine.TestDescriptor;
import org.junit.platform.engine.UniqueId;
import org.junit.platform.engine.support.descriptor.AbstractTestDescriptor;

import static org.junit.platform.engine.TestDescriptor.Type.TEST;

class ExpressionTestDescriptor extends AbstractTestDescriptor {

  private final String expression;

  private final String expected;

  ExpressionTestDescriptor(TestDescriptor parent, String expression) {
    this(parent, expression, "");
  }

  ExpressionTestDescriptor(TestDescriptor parent, String expression, String expected) {
    super(uniqueIdFor(parent, expression, expected), displayNameFor(expression, expected));

    this.expression = expression;
    this.expected = expected;
  }

  @Override
  public Type getType() {
    return TEST;
  }

  String getExpression() {
    return expression;
  }

  String getExpected() {
    return expected;
  }

  private static UniqueId uniqueIdFor(TestDescriptor parent, String expression, String expected) {
    var uniqueId = parent.getUniqueId().append("expression", expression);

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
