package name.sargon.descriptors;

import org.junit.platform.engine.UniqueId;
import org.junit.platform.engine.support.descriptor.AbstractTestDescriptor;

public abstract class ExpressionDescriptor extends AbstractTestDescriptor {

  private final long expressionIndex;

  protected ExpressionDescriptor(UniqueId uniqueId, String displayName, long expressionIndex) {
    super(uniqueId, displayName);
    this.expressionIndex = expressionIndex;
  }

  public long getExpressionIndex() {
    return expressionIndex;
  }

}
