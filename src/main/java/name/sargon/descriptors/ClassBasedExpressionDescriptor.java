package name.sargon.descriptors;

import org.junit.platform.engine.TestDescriptor;
import org.junit.platform.engine.UniqueId;
import org.junit.platform.engine.support.descriptor.AbstractTestDescriptor;
import org.junit.platform.engine.support.descriptor.ClassSource;

import static java.lang.String.format;
import static org.junit.platform.commons.support.ReflectionSupport.tryToLoadClass;
import static org.junit.platform.engine.TestDescriptor.Type.CONTAINER;

public class ClassBasedExpressionDescriptor extends AbstractTestDescriptor {

  public static final String CLASS_SEGMENT_NAME = "class";

  public ClassBasedExpressionDescriptor(TestDescriptor parent, Class<?> clazz) {
    super(uniqueIdFor(parent, clazz), displayNameFor(clazz), ClassSource.from(clazz));
  }

  @Override
  public Type getType() {
    return CONTAINER;
  }

  public Class<?> getReferencedClass() {
    var classSegment = getUniqueId().getSegments().stream()
            .filter(segment -> CLASS_SEGMENT_NAME.equals(segment.getType()))
            .findFirst()
            .orElseThrow(() -> new RuntimeException(format("Failed to determine class segment for %s", getUniqueId())));

    var fullyQualifiedClassName = classSegment.getValue();

    return tryToLoadClass(fullyQualifiedClassName)
            .getOrThrow(e -> new RuntimeException(format("Failed to load class %s", fullyQualifiedClassName), e));
  }

  private static UniqueId uniqueIdFor(TestDescriptor parent, Class<?> clazz) {
    return parent.getUniqueId().append(CLASS_SEGMENT_NAME, clazz.getName());
  }

  private static String displayNameFor(Class<?> clazz) {
    return clazz.getSimpleName();
  }

}
