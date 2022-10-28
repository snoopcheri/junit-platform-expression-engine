package name.sargon;

import name.sargon.ExpressionAnnotations.ConstantExpression;
import name.sargon.ExpressionAnnotations.Expressions;
import name.sargon.ExpressionAnnotations.ExpressionsResource;
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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.net.URI;

import static java.lang.String.format;
import static java.lang.reflect.Modifier.isStatic;
import static name.sargon.ExpressionTestDiscoverer.ExpressionResourceAnnotatedClassGenerator.generateForExpressionResourceAnnotation;
import static name.sargon.ExpressionTestDiscoverer.ExpressionsAnnotatedClassGenerator.generateForExpressionAnnotation;
import static name.sargon.ExpressionTestEngine.isCompatibleWithEngine;
import static name.sargon.descriptors.ClassBasedExpressionDescriptor.CLASS_SEGMENT_NAME;
import static org.junit.platform.commons.support.HierarchyTraversalMode.TOP_DOWN;
import static org.junit.platform.commons.support.ReflectionSupport.tryToLoadClass;

class ExpressionTestDiscoverer {

  /**
   * Discovers tests for {@link ClasspathRootSelector}.
   */
  static void discover(URI root, TestDescriptor parent) {
    var classes = ReflectionSupport.findAllClassesInClasspathRoot(
            root,
            ExpressionTestDiscoverer::hasExpressionAnnotations,
            className -> true
    );

    classes.forEach(clazz -> discover(clazz, parent));
  }

  /**
   * Discovers tests for {@link ClassSelector}.
   */
  static void discover(Class<?> clazz, TestDescriptor parent) {
    var hasExpressions = clazz.isAnnotationPresent(Expressions.class);
    var isExpressionsResource = clazz.isAnnotationPresent(ExpressionsResource.class);

    if (!hasExpressions && !isExpressionsResource) {
      return;
    }

    var classTestDescriptor = new ClassBasedExpressionDescriptor(parent, clazz);
    parent.addChild(classTestDescriptor);

    if (hasExpressions) {
      generateForExpressionAnnotation(clazz, classTestDescriptor);
    }

    if (isExpressionsResource) {
      generateForExpressionResourceAnnotation(
              clazz,
              clazz.getAnnotation(ExpressionsResource.class).file(),
              classTestDescriptor);
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

  private static boolean hasExpressionAnnotations(Class<?> clazz) {
    return clazz.isAnnotationPresent(Expressions.class)
            || clazz.isAnnotationPresent(ExpressionsResource.class);
  }

  static class ExpressionsAnnotatedClassGenerator {
    static void generateForExpressionAnnotation(Class<?> clazz, TestDescriptor parent) {
      var constantExpressionFields = ReflectionSupport.findFields(
              clazz,
              field -> field.isAnnotationPresent(ConstantExpression.class) && isStatic(field.getModifiers()) && field.getType() == String.class,
              TOP_DOWN
      );

      constantExpressionFields.forEach(field -> addConstantExpression(clazz, field, parent));

      var variableExpressionFields = ReflectionSupport.findFields(
              clazz,
              field -> field.isAnnotationPresent(VariableExpression.class) && isStatic(field.getModifiers()) && field.getType() == String.class,
              TOP_DOWN
      );

      variableExpressionFields.forEach(field -> addVariableExpression(clazz, field, parent));
    }

    private static void addConstantExpression(Class<?> clazz, Field field, TestDescriptor parent) {
      var expression = getStringValueOf(field, clazz);
      var annotation = field.getAnnotation(ConstantExpression.class);
      var expected = annotation.expected();
      var descriptor = new ConstantExpressionDescriptor(parent, expression, expected);

      parent.addChild(descriptor);
    }

    private static void addVariableExpression(Class<?> clazz, Field field, TestDescriptor parent) {
      var expression = getStringValueOf(field, clazz);
      var annotation = field.getAnnotation(VariableExpression.class);
      var from = annotation.from();
      var to = annotation.to();
      var descriptor = new VariableExpressionDescriptor(parent, expression, from, to);

      parent.addChild(descriptor);
    }

    private static String getStringValueOf(Field field, Class<?> clazz) {
      try {
        return field.get(clazz).toString();
      } catch (IllegalAccessException e) {
        throw new RuntimeException(e);
      }
    }
  }

  static class ExpressionResourceAnnotatedClassGenerator {

    static void generateForExpressionResourceAnnotation(Class<?> clazz, String resourceFile, TestDescriptor parent) {
      try (var is = clazz.getClassLoader().getResourceAsStream(resourceFile)) {
        if (is == null) {
          throw resourceFileAccessException(resourceFile);
        }

        new BufferedReader(new InputStreamReader(is)).lines().forEach(expression -> {
          var descriptor = new ConstantExpressionDescriptor(parent, expression);
          parent.addChild(descriptor);
        });
      } catch (IOException exc) {
        throw resourceFileAccessException(resourceFile);
      }

    }

    private static RuntimeException resourceFileAccessException(String resourceFile) {
      throw new IllegalArgumentException(format("Could not read expression resource file '%s')", resourceFile));
    }

  }

}
