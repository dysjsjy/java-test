package com.dysjsjy.组合;

public class MinusExpression extends BinaryOperatorExpression {

    public MinusExpression(Expression left, Expression right) {
        super(left, right);
    }

    @Override
    public int getValue() {
        return left.getValue() - right.getValue();
    }
}
