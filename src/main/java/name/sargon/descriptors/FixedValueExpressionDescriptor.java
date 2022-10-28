package name.sargon.descriptors;

import org.junit.platform.engine.TestDescriptor;
import org.junit.platform.engine.UniqueId;
import org.junit.platform.engine.support.descriptor.AbstractTestDescriptor;

import static java.lang.String.format;
import static java.lang.String.valueOf;
import static org.junit.platform.engine.TestDescriptor.Type.TEST;

public class FixedValueExpressionDescriptor extends AbstractTestDescriptor {

  private final long fixedValue;

  public FixedValueExpressionDescriptor(TestDescriptor parent, long fixedValue, String expression) {
    super(uniqueIdFor(parent, fixedValue), displayNameFor(fixedValue, expression));
    this.fixedValue = fixedValue;
  }

  @Override
  public Type getType() {
    return TEST;
  }

  public long getFixedValue() {
    return fixedValue;
  }

  private static UniqueId uniqueIdFor(TestDescriptor parent, long fixedValue) {
    return parent.getUniqueId()
            .append("fixed-value", valueOf(fixedValue));
  }

  private static String displayNameFor(long fixedValue, String expression) {
    return format("%s for x=%d", expression, fixedValue);
  }

}
