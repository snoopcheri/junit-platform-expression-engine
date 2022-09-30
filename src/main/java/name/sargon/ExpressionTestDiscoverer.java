package name.sargon;

import name.sargon.ExpressionAnnotations.Expression;
import name.sargon.ExpressionAnnotations.Expressions;
import name.sargon.ExpressionAnnotations.ExpressionsResource;
import name.sargon.descriptors.ClassBasedExpressionsTestDescriptor;
import name.sargon.descriptors.ExpressionTestDescriptor;
import org.junit.platform.commons.support.ReflectionSupport;
import org.junit.platform.engine.TestDescriptor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.net.URI;

import static java.lang.String.format;
import static java.lang.reflect.Modifier.isStatic;
import static name.sargon.ExpressionTestDiscoverer.ExpressionResourceAnnotatedClassGenerator.generateForExpressionResourceAnnotation;
import static name.sargon.ExpressionTestDiscoverer.ExpressionsAnnotatedClassGenerator.generateForExpressionAnnotation;
import static org.junit.platform.commons.support.HierarchyTraversalMode.TOP_DOWN;

class ExpressionTestDiscoverer {

  static void discover(URI root, TestDescriptor parent) {
    var classes = ReflectionSupport.findAllClassesInClasspathRoot(
            root,
            ExpressionTestDiscoverer::hasExpressionAnnotations,
            className -> true
    );

    classes.forEach(clazz -> discover(clazz, parent));
  }

  static void discover(Class<?> clazz, TestDescriptor parent) {
    var hasExpressions = clazz.isAnnotationPresent(Expressions.class);
    var isExpressionsResource = clazz.isAnnotationPresent(ExpressionsResource.class);

    if (!hasExpressions && !isExpressionsResource) {
      return;
    }

    var classTestDescriptor = new ClassBasedExpressionsTestDescriptor(parent, clazz);
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

  private static boolean hasExpressionAnnotations(Class<?> clazz) {
    return clazz.isAnnotationPresent(Expressions.class)
            || clazz.isAnnotationPresent(ExpressionsResource.class);
  }

  static class ExpressionsAnnotatedClassGenerator {
    static void generateForExpressionAnnotation(Class<?> clazz, TestDescriptor parent) {
      var fields = ReflectionSupport.findFields(
              clazz,
              field -> field.isAnnotationPresent(Expression.class) && isStatic(field.getModifiers()) && field.getType() == String.class,
              TOP_DOWN
      );

      fields.forEach(field -> generate(clazz, field, parent));
    }

    private static void generate(Class<?> clazz, Field field, TestDescriptor parent) {
      var expression = getStringValueOf(field, clazz);
      var annotation = field.getAnnotation(Expression.class);
      var expected = annotation.expected();
      var descriptor = new ExpressionTestDescriptor(parent, expression, expected);

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
          var descriptor = new ExpressionTestDescriptor(parent, expression);
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
