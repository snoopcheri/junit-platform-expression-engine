package name.sargon.descriptors;

import org.junit.platform.engine.TestDescriptor;
import org.junit.platform.engine.UniqueId;

import static java.lang.String.format;
import static java.lang.String.valueOf;
import static org.junit.platform.engine.TestDescriptor.Type.TEST;

public class ConstantExpressionDescriptor extends ExpressionDescriptor {

  public ConstantExpressionDescriptor(TestDescriptor parent, long constExpressionIndex, String constExpression) {
    super(
            uniqueIdFor(parent, constExpressionIndex),
            displayNameFor(constExpressionIndex, constExpression),
            constExpressionIndex
    );
  }

  @Override
  public Type getType() {
    return TEST;
  }

  private static UniqueId uniqueIdFor(TestDescriptor parent, long constExpressionIndex) {
    return parent.getUniqueId()
            .append("const-expression-index", valueOf(constExpressionIndex));
  }

  private static String displayNameFor(long constExpressionIndex, String constExpression) {
    return format("%d: %s", constExpressionIndex, constExpression);
  }

}
