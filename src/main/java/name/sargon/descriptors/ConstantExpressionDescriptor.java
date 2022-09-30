package name.sargon.descriptors;

import org.junit.platform.engine.TestDescriptor;
import org.junit.platform.engine.UniqueId;
import org.junit.platform.engine.support.descriptor.AbstractTestDescriptor;

import static org.junit.platform.engine.TestDescriptor.Type.TEST;

public class ConstantExpressionDescriptor extends AbstractTestDescriptor {

  private final String constExpression;

  private final String expected;

  public ConstantExpressionDescriptor(TestDescriptor parent, String constExpression) {
    this(parent, constExpression, "");
  }

  public ConstantExpressionDescriptor(TestDescriptor parent, String constExpression, String expected) {
    super(uniqueIdFor(parent, constExpression, expected), displayNameFor(constExpression, expected));

    this.constExpression = constExpression;
    this.expected = expected;
  }

  @Override
  public Type getType() {
    return TEST;
  }

  public String getConstExpression() {
    return constExpression;
  }

  public String getExpected() {
    return expected;
  }

  private static UniqueId uniqueIdFor(TestDescriptor parent, String expression, String expected) {
    var uniqueId = parent.getUniqueId().append("const-expression", expression);

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
