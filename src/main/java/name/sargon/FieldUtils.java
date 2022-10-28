package name.sargon;

import name.sargon.ExpressionAnnotations.ConstantExpression;
import name.sargon.ExpressionAnnotations.VariableExpression;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.List;

import static java.lang.reflect.Modifier.isStatic;
import static org.junit.platform.commons.support.HierarchyTraversalMode.TOP_DOWN;
import static org.junit.platform.commons.support.ReflectionSupport.findFields;

class FieldUtils {

  static List<Field> fieldsForConstantExpressions(Class<?> clazz) {
    return fieldsForAnnotation(clazz, ConstantExpression.class);
  }

  static List<Field> fieldsForVariableExpressions(Class<?> clazz) {
    return fieldsForAnnotation(clazz, VariableExpression.class);
  }

  static Field fieldForConstantExpression(Class<?> clazz, long fieldIndex) {
    return fieldsForConstantExpressions(clazz).get((int) fieldIndex);
  }

  static Field fieldForVariableExpression(Class<?> clazz, long fieldIndex) {
    return fieldsForVariableExpressions(clazz).get((int) fieldIndex);
  }

  static String stringValueOf(Field field, Class<?> clazz) {
    try {
      return field.get(clazz).toString();
    } catch (IllegalAccessException e) {
      throw new RuntimeException(e);
    }
  }

  private static List<Field> fieldsForAnnotation(Class<?> clazz, Class<? extends Annotation> annotationClass) {
    return findFields(
            clazz,
            field -> field.isAnnotationPresent(annotationClass) && isStatic(field.getModifiers()) && field.getType() == String.class,
            TOP_DOWN
    );
  }

}
