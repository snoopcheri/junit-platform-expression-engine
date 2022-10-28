package name.sargon.descriptors;

import org.junit.platform.engine.TestDescriptor;
import org.junit.platform.engine.UniqueId;
import org.junit.platform.engine.support.descriptor.AbstractTestDescriptor;

import static org.junit.platform.engine.TestDescriptor.Type.TEST;

public class ConstantExpressionDescriptor extends AbstractTestDescriptor {

  private final String constExpression;

  public ConstantExpressionDescriptor(TestDescriptor parent, String constExpression) {
    super(uniqueIdFor(parent, constExpression), displayNameFor(constExpression));

    this.constExpression = constExpression;
  }

  @Override
  public Type getType() {
    return TEST;
  }

  public String getConstExpression() {
    return constExpression;
  }

  private static UniqueId uniqueIdFor(TestDescriptor parent, String expression) {
    return parent.getUniqueId()
            .append("const-expression", expression);
  }

  private static String displayNameFor(String expression) {
    return expression;
  }

}
