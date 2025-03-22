package com.dysjsjy.组合;

public class DevisionExpression extends BinaryOperatorExpression {

    public DevisionExpression(Expression left, Expression right) {
        super(left, right);
    }

    @Override
    public int getValue() {
        return left.getValue() / right.getValue();
    }
}
