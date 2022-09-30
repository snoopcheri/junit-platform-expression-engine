package name.sargon;

import name.sargon.ExpressionAnnotations.Expression;
import name.sargon.ExpressionAnnotations.Expressions;
import name.sargon.ExpressionAnnotations.ExpressionsResource;
import org.junit.platform.commons.support.ReflectionSupport;
import org.junit.platform.engine.TestDescriptor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.net.URI;

import static java.lang.String.format;
import static java.lang.reflect.Modifier.isStatic;
import static name.sargon.ExpressionTestDescriptorFactory.ExpressionResourceAnnotatedClassGenerator.generateForExpressionResourceAnnotation;
import static name.sargon.ExpressionTestDescriptorFactory.ExpressionsAnnotatedClassGenerator.generateForExpressionAnnotation;
import static org.junit.platform.commons.support.HierarchyTraversalMode.TOP_DOWN;

class ExpressionTestDescriptorFactory {

  static void generate(URI root, TestDescriptor parentDescriptor) {
    var classes = ReflectionSupport.findAllClassesInClasspathRoot(
            root,
            ExpressionTestDescriptorFactory::hasExpressionAnnotations,
            className -> true
    );

    classes.forEach(clazz -> generate(clazz, parentDescriptor));
  }

  static void generate(Class<?> clazz, TestDescriptor parentDescriptor) {
    if (clazz.isAnnotationPresent(Expressions.class)) {
      generateForExpressionAnnotation(clazz, parentDescriptor);
    }

    if (clazz.isAnnotationPresent(ExpressionsResource.class)) {
      generateForExpressionResourceAnnotation(
              clazz,
              clazz.getAnnotation(ExpressionsResource.class).file(),
              parentDescriptor);
    }
  }

  private static boolean hasExpressionAnnotations(Class<?> clazz) {
    return clazz.isAnnotationPresent(Expressions.class)
            || clazz.isAnnotationPresent(ExpressionsResource.class);
  }

  static class ExpressionsAnnotatedClassGenerator {
    static void generateForExpressionAnnotation(Class<?> clazz, TestDescriptor parentDescriptor) {
      var fields = ReflectionSupport.findFields(
              clazz,
              field -> field.isAnnotationPresent(Expression.class) && isStatic(field.getModifiers()) && field.getType() == String.class,
              TOP_DOWN
      );

      fields.forEach(field -> generate(clazz, field, parentDescriptor));
    }

    private static void generate(Class<?> clazz, Field field, TestDescriptor parentDescriptor) {
      var expression = getStringValueOf(field, clazz);
      var annotation = field.getAnnotation(Expression.class);
      var expected = annotation.expected();
      var descriptor = new ExpressionTestDescriptor(expression, expected);

      parentDescriptor.addChild(descriptor);
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

    static void generateForExpressionResourceAnnotation(Class<?> clazz, String resourceFile, TestDescriptor parentDescriptor) {
      try (var is = clazz.getClassLoader().getResourceAsStream(resourceFile)) {
        if (is == null) {
          throw resourceFileAccessException(resourceFile);
        }

        new BufferedReader(new InputStreamReader(is)).lines().forEach(expression -> {
          var descriptor = new ExpressionTestDescriptor(expression, "");
          parentDescriptor.addChild(descriptor);
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
