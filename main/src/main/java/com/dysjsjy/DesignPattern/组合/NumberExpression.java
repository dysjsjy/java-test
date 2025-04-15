package com.dysjsjy.DesignPattern.组合;

public class NumberExpression implements Expression {
    private final int value;

    public NumberExpression(int value) {
        this.value = value;
    }

    @Override
    public int getValue() {
        return this.value;
    }
}
