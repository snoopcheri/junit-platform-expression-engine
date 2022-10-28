package name.sargon.descriptors;

import org.junit.platform.engine.TestDescriptor;
import org.junit.platform.engine.UniqueId;

import static java.lang.String.format;
import static java.lang.String.valueOf;
import static org.junit.platform.engine.TestDescriptor.Type.CONTAINER_AND_TEST;

public class VariableExpressionDescriptor extends ExpressionDescriptor {

  public VariableExpressionDescriptor(TestDescriptor parent, long varExpressionIndex, String varExpression, String from, String to) {
    super(
            uniqueIdFor(parent, varExpressionIndex),
            displayNameFor(varExpressionIndex, varExpression, from, to),
            varExpressionIndex
    );
  }

  @Override
  public Type getType() {
    return CONTAINER_AND_TEST;
  }

  private static UniqueId uniqueIdFor(TestDescriptor parent, long varExpressionIndex) {
    return parent.getUniqueId()
            .append("var-expression-index", valueOf(varExpressionIndex));
  }

  static String displayNameFor(long varExpressionIndex, String varExpression, String from, String to) {
    return format("%d: %s {from=%s, to=%s}", varExpressionIndex, varExpression, from, to);
  }

}
