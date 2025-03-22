package com.dysjsjy.设计模式.策略;

public class Main {
    public static void main(String[] args) {

    }
}

interface Strategy {
    int execute(int a, int b);
}

class AddStrategy implements Strategy {
    public int execute(int a, int b) {
        return a + b;
    }
}

class MultiplyStrategy implements Strategy {
    public int execute(int a, int b) {
        return a * b;
    }
}

class Context {
    private Strategy strategy;

    public void setStrategy(Strategy strategy) {
        this.strategy = strategy;
    }

    public int executeStrategy(int a, int b) {
        return strategy.execute(a, b);
    }
}