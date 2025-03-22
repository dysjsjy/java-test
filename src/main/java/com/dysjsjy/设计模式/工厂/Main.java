package com.dysjsjy.设计模式.工厂;

// 太弱智了
public class Main {

    public static void main(String[] args) {
        Shape circle = new Circle();
        circle.draw();
    }
}

interface Shape {
    void draw();
}

class Circle implements Shape {
    @Override
    public void draw() {
        System.out.println("画了个圆。");
    }
}

class Retangle implements Shape {
    @Override
    public void draw() {
        System.out.println("画了个矩形。");
    }
}