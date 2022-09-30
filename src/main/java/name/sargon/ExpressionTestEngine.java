package name.sargon;

import org.junit.platform.engine.*;
import org.junit.platform.engine.discovery.ClassSelector;
import org.junit.platform.engine.discovery.ClasspathRootSelector;
import org.junit.platform.engine.support.descriptor.EngineDescriptor;

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
            .forEach(selector -> ExpressionTestDiscoverer.discover(selector.getClasspathRoot(), engineDescriptor));

    request.getSelectorsByType(ClassSelector.class)
            .forEach(selector -> ExpressionTestDiscoverer.discover(selector.getJavaClass(), engineDescriptor));

    return engineDescriptor;
  }

  @Override
  public void execute(ExecutionRequest request) {
    var root = request.getRootTestDescriptor();

    if (root instanceof EngineDescriptor engineDescriptor) {
      var executionListener = request.getEngineExecutionListener();
      var executor = new ExpressionTestExecutor(executionListener);

      executor.execute(engineDescriptor);
    }
  }

}
