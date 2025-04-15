package com.dysjsjy.DesignPattern.单例;

public class Main {

    public Main() {
    }

    public static void main(String[] args) {
        Singleton.INSTANCE.doSomething();
        Main instance = Main.getInstance();
    }

    // 推荐静态内部类
    private static class Holder {
        private final static Main INSTANCE = new Main();
    }

    public static Main getInstance() {
        return Holder.INSTANCE;
    }
}

// 最推荐枚举实现单例，可以防反序列化破坏和反射破坏，
enum Singleton {
    INSTANCE;
    public void doSomething() {
        System.out.println("Singleton is working");
    }
}