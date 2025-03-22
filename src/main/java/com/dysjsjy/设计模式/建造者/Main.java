package com.dysjsjy.设计模式.建造者;

public class Main {

    public static void main(String[] args) {

    }
}


// 把类的构造器独立成一个内部类，使用这个内部类来创建类
class User {
    private final String name;
    private final int age;

    private User(Builder builder) {
        this.name = builder.name;
        this.age = builder.age;
    }

    public static class Builder {
        private String name;
        private int age;

        public Builder setName(String name) {
            this.name = name;
            return this;
        }

        public Builder setAge(int age) {
            this.age = age;
            return this;
        }

        public User build() {
            return new User(this);
        }
    }

    public static void main(String[] args) {
        User user = new User.Builder()
                .setName("Alice")
                .setAge(25)
                .build();
        System.out.println("Name: " + user.name + ", Age: " + user.age);
    }
}
