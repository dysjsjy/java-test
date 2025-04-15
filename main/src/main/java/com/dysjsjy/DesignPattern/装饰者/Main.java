package com.dysjsjy.DesignPattern.装饰者;

public class Main {

    public static void main(String[] args) {
        Coffee simpleCoffee = new SimpleCoffee();
        MilkDecorator milkDecorator = new MilkDecorator(simpleCoffee);
        System.out.println(milkDecorator.getDescription() + milkDecorator.cost());
    }
}

// 用MilkDecorator包裹SimpleCoffee为其添加新的装饰，
interface Coffee {
    String getDescription();
    double cost();
}

class SimpleCoffee implements Coffee {
    public String getDescription() {
        return "Simple Coffee";
    }

    public double cost() {
        return 5.0;
    }
}

class MilkDecorator implements Coffee {
    private Coffee coffee;

    public MilkDecorator(Coffee coffee) {
        this.coffee = coffee;
    }

    public String getDescription() {
        return coffee.getDescription() + ", Milk";
    }

    public double cost() {
        return coffee.cost() + 1.5;
    }
}