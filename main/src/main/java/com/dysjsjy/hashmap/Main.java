package com.dysjsjy.hashmap;

import org.junit.Test;

public class Main {
    @Test
    public void f1() {
        MyHashMap2<Integer, Integer> map2 = new MyHashMap2();
        for (int i = 0; i < 1000000; i++) {
            map2.put(i, i);
        }
    }
}
