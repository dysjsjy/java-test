package com.dysjsjy.DesignPattern.组合;

public class AddExpression extends BinaryOperatorExpression {

    public AddExpression(Expression left, Expression right) {
        super(left, right);
    }

    @Override
    public int getValue() {
        return left.getValue() + right.getValue();
    }
}
