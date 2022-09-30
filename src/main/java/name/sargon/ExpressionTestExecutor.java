package name.sargon;

import name.sargon.descriptors.ClassBasedExpressionsTestDescriptor;
import name.sargon.descriptors.ExpressionTestDescriptor;
import net.objecthunter.exp4j.ExpressionBuilder;
import org.junit.platform.engine.EngineExecutionListener;
import org.opentest4j.AssertionFailedError;

import static java.lang.String.format;
import static org.junit.platform.engine.TestExecutionResult.failed;
import static org.junit.platform.engine.TestExecutionResult.successful;

public class ExpressionTestExecutor {

  private final EngineExecutionListener executionListener;

  public ExpressionTestExecutor(EngineExecutionListener executionListener) {
    this.executionListener = executionListener;
  }

  void execute(ClassBasedExpressionsTestDescriptor classBasedDescriptor) {
    try {
      executionListener.executionStarted(classBasedDescriptor);

      classBasedDescriptor.getChildren().stream()
              .filter(descriptor -> descriptor instanceof ExpressionTestDescriptor)
              .map(ExpressionTestDescriptor.class::cast)
              .forEachOrdered(this::execute);

      executionListener.executionFinished(classBasedDescriptor, successful());
    } catch (Throwable failure) {
      executionListener.executionFinished(classBasedDescriptor, failed(failure));
    }
  }

  private void execute(ExpressionTestDescriptor descriptor) {
    try {
      executionListener.executionStarted(descriptor);

      var expression = descriptor.getExpression();
      var expected = descriptor.getExpected();
      var actual = (int) new ExpressionBuilder(expression).build().evaluate();

      showAndVerify(expression, expected, actual);

      executionListener.executionFinished(descriptor, successful());

    } catch (Throwable failure) {
      executionListener.executionFinished(descriptor, failed(failure));
    }
  }

  private static void showAndVerify(String expression, String expected, int actual) {
    if (expected.isBlank()) {
      showResult(expression, actual, "");
    } else {
      var value = Integer.parseInt(expected);
      if (value == actual) {
        showResult(expression, actual, " ✓");
      } else {
        showResult(expression, actual, format(" ❌ (expected: %d)", value));
        throw new AssertionFailedError(format("expected: %s, but was: %d", expected, actual));
      }
    }
  }

  private static void showResult(String expression, int actual, String verificationText) {
    System.out.printf("-> %s = %d%s%n", expression, actual, verificationText);
  }

}
