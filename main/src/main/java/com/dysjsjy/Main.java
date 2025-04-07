package com.dysjsjy;

public class Main {
    public static void main(String[] args) {
        Son son = new Son();
    }
}

class Father {
    int x = 10;

    public Father() {
        this.print();
        x = 20;
    }

    void print() {
        System.out.println(x);
    }
}

class Son extends Father {
    int x = 30;

    public Son() {
        this.print();
        x = 40;
    }

    void print() {
        System.out.println(x);
    }
}