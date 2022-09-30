package name.sargon.descriptors;

import org.junit.platform.engine.TestDescriptor;
import org.junit.platform.engine.UniqueId;
import org.junit.platform.engine.support.descriptor.AbstractTestDescriptor;

import static java.lang.String.format;
import static org.junit.platform.engine.TestDescriptor.Type.CONTAINER_AND_TEST;

public class VariableExpressionDescriptor extends AbstractTestDescriptor {

  private final String varExpression;
  private final String from;
  private final String to;

  public VariableExpressionDescriptor(TestDescriptor parent, String varExpression, String from, String to) {
    super(uniqueIdFor(parent, varExpression, from, to), displayNameFor(varExpression, from, to));

    this.varExpression = varExpression;
    this.from = from;
    this.to = to;
  }

  @Override
  public Type getType() {
    return CONTAINER_AND_TEST;
  }

  public String getVarExpression() {
    return varExpression;
  }

  public String getFrom() {
    return from;
  }

  public String getTo() {
    return to;
  }

  private static UniqueId uniqueIdFor(TestDescriptor parent, String expression, String from, String to) {
    return parent.getUniqueId().append("var-expression", expression);
  }

  static String displayNameFor(String expression, String from, String to) {
    return format("%s {from=%s, to=%s}", expression, from, to);
  }

}
