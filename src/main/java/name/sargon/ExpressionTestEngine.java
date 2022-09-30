package name.sargon;

import org.junit.platform.engine.*;
import org.junit.platform.engine.discovery.ClassSelector;
import org.junit.platform.engine.discovery.ClasspathRootSelector;
import org.junit.platform.engine.support.descriptor.EngineDescriptor;

import static name.sargon.ExpressionTestDescriptorFactory.generate;
import static org.junit.platform.engine.TestExecutionResult.failed;
import static org.junit.platform.engine.TestExecutionResult.successful;

public class ExpressionTestEngine implements TestEngine {

  public static final String EXPRESSION_TEST_ENGINE_ID = "expression-test-engine";

  @Override
  public String getId() {
    return EXPRESSION_TEST_ENGINE_ID;
  }

  @Override
  public TestDescriptor discover(EngineDiscoveryRequest request, UniqueId uniqueId) {
    var engineDescriptor = new EngineDescriptor(uniqueId, "Expression Test Engine");

    request.getSelectorsByType(ClasspathRootSelector.class)
            .forEach(selector -> generate(selector.getClasspathRoot(), engineDescriptor));

    request.getSelectorsByType(ClassSelector.class)
            .forEach(selector -> generate(selector.getJavaClass(), engineDescriptor));

    return engineDescriptor;
  }

  @Override
  public void execute(ExecutionRequest request) {
    var root = request.getRootTestDescriptor();
    var executionListener = request.getEngineExecutionListener();
    var expressionTestExecutor = new ExpressionTestExecutor(root, executionListener);

    try {
      executionListener.executionStarted(root);

      root.getChildren().stream()
              .filter(descriptor -> descriptor instanceof ExpressionTestDescriptor)
              .map(ExpressionTestDescriptor.class::cast)
              .forEach(expressionTestExecutor::execute);

      executionListener.executionFinished(root, successful());
    } catch (Throwable failure) {
      executionListener.executionFinished(root, failed(failure));
    }
  }

}
