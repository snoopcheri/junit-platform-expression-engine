package name.sargon;

import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;
import org.junit.platform.engine.EngineExecutionListener;
import org.junit.platform.engine.TestDescriptor;
import org.opentest4j.AssertionFailedError;

import static java.lang.String.format;
import static org.junit.platform.engine.TestExecutionResult.failed;
import static org.junit.platform.engine.TestExecutionResult.successful;

public class ExpressionTestExecutor {

  private final TestDescriptor root;
  private final EngineExecutionListener executionListener;

  public ExpressionTestExecutor(TestDescriptor root, EngineExecutionListener executionListener) {
    this.root = root;
    this.executionListener = executionListener;
  }

  void execute(ExpressionTestDescriptor descriptor) {
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
