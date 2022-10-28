package name.sargon;

import com.codepoetics.protonpack.Indexed;
import name.sargon.ExpressionAnnotations.Expressions;
import name.sargon.ExpressionAnnotations.VariableExpression;
import name.sargon.descriptors.ClassBasedExpressionDescriptor;
import name.sargon.descriptors.ConstantExpressionDescriptor;
import name.sargon.descriptors.VariableExpressionDescriptor;
import org.junit.platform.commons.support.ReflectionSupport;
import org.junit.platform.engine.TestDescriptor;
import org.junit.platform.engine.UniqueId;
import org.junit.platform.engine.discovery.ClassSelector;
import org.junit.platform.engine.discovery.ClasspathRootSelector;
import org.junit.platform.engine.discovery.UniqueIdSelector;

import java.lang.reflect.Field;
import java.net.URI;

import static com.codepoetics.protonpack.StreamUtils.zipWithIndex;
import static name.sargon.ExpressionTestDiscoverer.ExpressionsAnnotatedClassGenerator.generateForExpressionAnnotation;
import static name.sargon.ExpressionTestEngine.isCompatibleWithEngine;
import static name.sargon.FieldUtils.*;
import static name.sargon.descriptors.ClassBasedExpressionDescriptor.CLASS_SEGMENT_NAME;
import static org.junit.platform.commons.support.ReflectionSupport.tryToLoadClass;

class ExpressionTestDiscoverer {

  /**
   * Discovers tests for {@link ClasspathRootSelector}.
   */
  static void discover(URI root, TestDescriptor parent) {
    var classes = ReflectionSupport.findAllClassesInClasspathRoot(
            root,
            clazz -> clazz.isAnnotationPresent(Expressions.class),
            className -> true
    );

    classes.forEach(clazz -> discover(clazz, parent));
  }

  /**
   * Discovers tests for {@link ClassSelector}.
   */
  static void discover(Class<?> clazz, TestDescriptor parent) {
    var hasExpressions = clazz.isAnnotationPresent(Expressions.class);

    if (hasExpressions) {
      var classTestDescriptor = new ClassBasedExpressionDescriptor(parent, clazz);
      parent.addChild(classTestDescriptor);
      generateForExpressionAnnotation(clazz, classTestDescriptor);
    }
  }


  /**
   * Discovers tests for {@link UniqueIdSelector}.
   */
  static void discover(UniqueId uniqueId, TestDescriptor parent) {
    if (!isCompatibleWithEngine(uniqueId)) {
      return;
    }

    // Since Test Distribution only distributes direct children of the engine level,
    // it is enough to consider the class level. When more specific unique IDs are
    // specified, the expression engine simply runs executes everything below the
    // class level.

    uniqueId.getSegments().stream()
            .filter(segment -> CLASS_SEGMENT_NAME.equals(segment.getType()))
            .findFirst()
            .ifPresent(segment -> {
              var fullyQualifiedClassName = segment.getValue();
              tryToLoadClass(fullyQualifiedClassName)
                      .ifSuccess(clazz -> discover(clazz, parent));
            });
  }

  static class ExpressionsAnnotatedClassGenerator {
    static void generateForExpressionAnnotation(Class<?> clazz, TestDescriptor parent) {
      zipWithIndex(fieldsForConstantExpressions(clazz).stream())
              .forEach(field -> addConstantExpression(clazz, field, parent));

      zipWithIndex(fieldsForVariableExpressions(clazz).stream())
              .forEach(field -> addVariableExpression(clazz, field, parent));
    }

    private static void addConstantExpression(Class<?> clazz, Indexed<Field> field, TestDescriptor parent) {
      var expression = stringValueOf(field.getValue(), clazz);
      var descriptor = new ConstantExpressionDescriptor(parent, field.getIndex(), expression);

      parent.addChild(descriptor);
    }

    private static void addVariableExpression(Class<?> clazz, Indexed<Field> field, TestDescriptor parent) {
      var expression = stringValueOf(field.getValue(), clazz);
      var annotation = field.getValue().getAnnotation(VariableExpression.class);
      var from = annotation.from();
      var to = annotation.to();
      var descriptor = new VariableExpressionDescriptor(parent, field.getIndex(), expression, from, to);

      parent.addChild(descriptor);
    }

  }

}
