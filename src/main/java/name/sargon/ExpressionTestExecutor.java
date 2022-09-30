package name.sargon;

import name.sargon.descriptors.ClassBasedExpressionDescriptor;
import name.sargon.descriptors.ExpressionDescriptor;
import net.objecthunter.exp4j.ExpressionBuilder;
import org.junit.platform.engine.EngineExecutionListener;
import org.junit.platform.engine.support.descriptor.EngineDescriptor;
import org.opentest4j.AssertionFailedError;

import static java.lang.String.format;
import static org.junit.platform.engine.TestExecutionResult.failed;
import static org.junit.platform.engine.TestExecutionResult.successful;

public class ExpressionTestExecutor {

  private final EngineExecutionListener executionListener;

  public ExpressionTestExecutor(EngineExecutionListener executionListener) {
    this.executionListener = executionListener;
  }

  void execute(EngineDescriptor root) {
    try {
      executionListener.executionStarted(root);

      root.getChildren().stream()
              .filter(descriptor -> descriptor instanceof ClassBasedExpressionDescriptor)
              .map(ClassBasedExpressionDescriptor.class::cast)
              .forEachOrdered(this::execute);

      executionListener.executionFinished(root, successful());
    } catch (Throwable failure) {
      executionListener.executionFinished(root, failed(failure));
    }
  }

  private void execute(ClassBasedExpressionDescriptor classBasedDescriptor) {
    try {
      executionListener.executionStarted(classBasedDescriptor);

      classBasedDescriptor.getChildren().stream()
              .filter(descriptor -> descriptor instanceof ExpressionDescriptor)
              .map(ExpressionDescriptor.class::cast)
              .forEachOrdered(this::execute);

      executionListener.executionFinished(classBasedDescriptor, successful());
    } catch (Throwable failure) {
      executionListener.executionFinished(classBasedDescriptor, failed(failure));
    }
  }

  private void execute(ExpressionDescriptor descriptor) {
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
