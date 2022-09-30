package name.sargon;

import net.objecthunter.exp4j.ExpressionBuilder;

public class Main {

  public static void main(String[] args) {
    var expression = new ExpressionBuilder("6*7").build();
    System.out.println("e=" + expression.evaluate());
  }

}
