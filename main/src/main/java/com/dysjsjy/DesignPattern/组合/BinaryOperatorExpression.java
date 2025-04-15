package com.dysjsjy.DesignPattern.组合;

public abstract class BinaryOperatorExpression implements Expression {
    Expression left;
    Expression right;

    public BinaryOperatorExpression(Expression left, Expression right) {
        this.left = left;
        this.right = right;
    }
}
