package com.dysjsjy.juc;

import java.util.*;

public class Main {

    public static void main(String[] args) {
        HashMap<Integer, Integer> map = new HashMap<>();
    }

    public static <U, T extends U> void f1(T...t) {
        System.out.println("hello" + t.toString());
    }

    public static <T> List<T> asList(T... a) {
        return List.of(a);
    }

    public <T> void sort(List<T> list, Comparator<? super T> c) {
        list.sort(c);
    }
}


class demo<T, U> {
    public static <U, T extends U> void f1(T...t) {
        System.out.println(t[t.length - 1]);
    }

    public static void main(String[] args) {
        f1(1, 2, 3);
    }
}