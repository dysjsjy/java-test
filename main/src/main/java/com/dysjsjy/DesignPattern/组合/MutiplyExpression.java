package com.dysjsjy.DesignPattern.组合;

public class MutiplyExpression extends BinaryOperatorExpression {

    public MutiplyExpression(Expression left, Expression right) {
        super(left, right);
    }

    @Override
    public int getValue() {
        return left.getValue() * right.getValue();
    }
}
