package com.dysjsjy.DesignPattern.组合;

public class Main {

    public static void main(String[] args) {
        //   1+15*(9+4+(1+5)) + 6
        ExpressionParser expressionParser = new ExpressionParser("1+15*(9+4+(1+5))+6");
        Expression parse = expressionParser.parse();
        System.out.println(parse.getValue());
        System.out.println(1+15*(9+4+(1+5)) + 6);
    }
}
