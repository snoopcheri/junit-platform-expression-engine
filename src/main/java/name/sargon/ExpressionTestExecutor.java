package name.sargon;

import name.sargon.ExpressionAnnotations.VariableExpression;
import name.sargon.descriptors.ClassBasedExpressionDescriptor;
import name.sargon.descriptors.ConstantExpressionDescriptor;
import name.sargon.descriptors.FixedValueExpressionDescriptor;
import name.sargon.descriptors.VariableExpressionDescriptor;
import net.objecthunter.exp4j.ExpressionBuilder;
import org.junit.platform.engine.EngineExecutionListener;
import org.junit.platform.engine.support.descriptor.EngineDescriptor;

import static java.lang.Integer.parseInt;
import static java.lang.String.valueOf;
import static name.sargon.FieldUtils.*;
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

      Class<?> testClass = classBasedDescriptor.getReferencedClass();

      classBasedDescriptor.getChildren().stream()
              .filter(descriptor -> descriptor instanceof ConstantExpressionDescriptor)
              .map(ConstantExpressionDescriptor.class::cast)
              .forEachOrdered(desc -> execute(testClass, desc));

      classBasedDescriptor.getChildren().stream()
              .filter(descriptor -> descriptor instanceof VariableExpressionDescriptor)
              .map(VariableExpressionDescriptor.class::cast)
              .forEachOrdered(desc -> execute(testClass, desc));

      executionListener.executionFinished(classBasedDescriptor, successful());
    } catch (Throwable failure) {
      executionListener.executionFinished(classBasedDescriptor, failed(failure));
    }
  }

  private void execute(Class<?> testClass, ConstantExpressionDescriptor descriptor) {
    try {
      executionListener.executionStarted(descriptor);

      var expressionField = fieldForConstantExpression(testClass, descriptor.getExpressionIndex());
      var constExpression = stringValueOf(expressionField, testClass);
      var evaluatedValue = (int) new ExpressionBuilder(constExpression).build().evaluate();

      System.out.printf("-> %s = %d%n", constExpression, evaluatedValue);

      executionListener.executionFinished(descriptor, successful());

    } catch (Throwable failure) {
      executionListener.executionFinished(descriptor, failed(failure));
    }
  }

  private void execute(Class<?> testClass, VariableExpressionDescriptor descriptor) {
    try {
      executionListener.executionStarted(descriptor);

      var expressionField = fieldForVariableExpression(testClass, descriptor.getExpressionIndex());
      var varExpression = stringValueOf(expressionField, testClass);
      var annotation = expressionField.getAnnotation(VariableExpression.class);
      var from = parseInt(annotation.from());
      var to = parseInt(annotation.to());

      for (int fixedValue = from; fixedValue <= to; fixedValue++) {
        var child = new FixedValueExpressionDescriptor(descriptor, fixedValue, varExpression);
        descriptor.addChild(child);
        executionListener.dynamicTestRegistered(child);

        execute(varExpression, child);
      }

      executionListener.executionFinished(descriptor, successful());
    } catch (Throwable failure) {
      executionListener.executionFinished(descriptor, failed(failure));
    }
  }

  private void execute(String varExpression, FixedValueExpressionDescriptor descriptor) {
    try {
      executionListener.executionStarted(descriptor);

      var expression = varExpression.replace("x", valueOf(descriptor.getFixedValue()));
      var evaluatedValue = (int) new ExpressionBuilder(expression).build().evaluate();

      System.out.printf("-> %s = %d%n", expression, evaluatedValue);

      executionListener.executionFinished(descriptor, successful());
    } catch (Throwable failure) {
      executionListener.executionFinished(descriptor, failed(failure));
    }
  }

}
