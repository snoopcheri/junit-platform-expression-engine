package name.sargon.descriptors;

import org.junit.platform.engine.TestDescriptor;
import org.junit.platform.engine.UniqueId;
import org.junit.platform.engine.support.descriptor.AbstractTestDescriptor;

import static org.junit.platform.engine.TestDescriptor.Type.TEST;

public class ExpressionDescriptor extends AbstractTestDescriptor {

  private final String expression;

  private final String expected;

  public ExpressionDescriptor(TestDescriptor parent, String expression) {
    this(parent, expression, "");
  }

  public ExpressionDescriptor(TestDescriptor parent, String expression, String expected) {
    super(uniqueIdFor(parent, expression, expected), displayNameFor(expression, expected));

    this.expression = expression;
    this.expected = expected;
  }

  @Override
  public Type getType() {
    return TEST;
  }

  public String getExpression() {
    return expression;
  }

  public String getExpected() {
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
