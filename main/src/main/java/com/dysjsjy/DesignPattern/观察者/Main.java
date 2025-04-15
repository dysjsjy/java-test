package com.dysjsjy.DesignPattern.观察者;

import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        Subject subject = new Subject();
        for (int i = 0; i < 3; i++) {
            subject.addObserver(new ConcreteObserver("ConcreteObserver (" + i + ") "));
        }
        subject.notifyObservers("hello");
    }
}

interface Observer {
    void update(String message);
}

class ConcreteObserver implements Observer {
    String name;

    public ConcreteObserver(String name) {
        this.name = name;
    }

    @Override
    public void update(String message) {
        System.out.println(name + "receive: " + message);
    }
}

class Subject {
    private final List<Observer> observers = new ArrayList<>();

    public void addObserver(Observer observer) {
        observers.add(observer);
    }

    public void notifyObservers(String message) {
        for (Observer observer : observers) {
            observer.update(message);
        }
    }
}