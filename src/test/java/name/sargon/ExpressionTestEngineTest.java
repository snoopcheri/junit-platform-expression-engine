package name.sargon;

import org.junit.jupiter.api.Test;
import org.junit.platform.testkit.engine.EngineTestKit;

import static name.sargon.ExpressionTestEngine.EXPRESSION_TEST_ENGINE_ID;
import static org.junit.platform.engine.discovery.DiscoverySelectors.selectClass;
import static org.junit.platform.testkit.engine.EventConditions.*;

public class ExpressionTestEngineTest {

  @Test
  void testExpressions() {
    // act
    var events = EngineTestKit.engine(EXPRESSION_TEST_ENGINE_ID)
            .selectors(selectClass(ExpressionsTest.class))
            .execute()
            .allEvents()
            .debug();

    // assert
    events.assertEventsMatchLooselyInOrder(
            event(engine(), started()),
            event(container(ExpressionsTest.class), started()),
            event(test("const-expression:6 * 7"), started()),
            event(test("const-expression:6 * 7"), finishedSuccessfully()),
            event(test("const-expression:2 * 3 * 7"), started()),
            event(test("const-expression:2 * 3 * 7"), finishedSuccessfully()),
            event(test("var-expression:x * x"), started()),
            event(test("var-expression:x * x"), finishedSuccessfully()),
            event(container(ExpressionsTest.class), finishedSuccessfully()),
            event(engine(), finishedSuccessfully())
    );
  }

  @Test
  void testExpressionsResource() {
    // act
    var events = EngineTestKit.engine(EXPRESSION_TEST_ENGINE_ID)
            .selectors(selectClass(ExpressionsResourceTest.class))
            .execute()
            .allEvents()
            .debug();

    // assert
    events.assertEventsMatchLooselyInOrder(
            event(engine(), started()),
            event(container(ExpressionsResourceTest.class), started()),
            event(test("1+2+3+4+5+6+7+8"), started()),
            event(test("1+2+3+4+5+6+7+8"), finishedSuccessfully()),
            event(container(ExpressionsResourceTest.class), finishedSuccessfully()),
            event(engine(), finishedSuccessfully())
    );
  }

}
