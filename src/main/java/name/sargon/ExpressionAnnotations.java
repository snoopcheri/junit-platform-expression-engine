package name.sargon;

import org.junit.platform.commons.annotation.Testable;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

public class ExpressionAnnotations {

  @Target(TYPE)
  @Retention(RUNTIME)
  @Testable
  @interface ExpressionsResource {
    String file();
  }

  @Target(TYPE)
  @Retention(RUNTIME)
  @Testable
  @interface Expressions {
  }

  @Target(ElementType.FIELD)
  @Retention(RUNTIME)
  @Testable
  @interface Expression {
    String expected() default "";
  }

}
