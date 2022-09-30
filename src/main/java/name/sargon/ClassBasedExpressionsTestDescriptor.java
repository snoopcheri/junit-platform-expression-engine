package name.sargon;

import org.junit.platform.engine.TestDescriptor;
import org.junit.platform.engine.UniqueId;
import org.junit.platform.engine.support.descriptor.AbstractTestDescriptor;

import static org.junit.platform.engine.TestDescriptor.Type.CONTAINER;

public class ClassBasedExpressionsTestDescriptor extends AbstractTestDescriptor {

  private final Class<?> clazz;

  public ClassBasedExpressionsTestDescriptor(TestDescriptor parent, Class<?> clazz) {
    super(uniqueIdFor(parent, clazz), displayNameFor(clazz));
    this.clazz = clazz;
  }

  @Override
  public Type getType() {
    return CONTAINER;
  }

  private static UniqueId uniqueIdFor(TestDescriptor parent, Class<?> clazz) {
    return parent.getUniqueId().append("class", clazz.getName());
  }

  private static String displayNameFor(Class<?> clazz) {
    return clazz.getSimpleName();
  }

}
