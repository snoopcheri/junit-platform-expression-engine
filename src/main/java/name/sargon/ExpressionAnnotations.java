package name.sargon;

import org.junit.platform.commons.annotation.Testable;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

public class ExpressionAnnotations {

  @Target(TYPE)
  @Retention(RUNTIME)
  @Testable
  @interface Expressions {
  }

  @Target(FIELD)
  @Retention(RUNTIME)
  @Testable
  @interface ConstantExpression {
    String expected() default "";
  }

  @Target(FIELD)
  @Retention(RUNTIME)
  @Testable
  @interface VariableExpression {
    String from() default "0";

    String to() default "0";
  }

}
