package com.dysjsjy.迭代器;

import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.NoSuchElementException;

// 关键词：Iterable, size, modCount, iterator, Itr, cursor, expectedModCount, hasNext, next,

public class User implements Iterable<String> {
    private String name;
    private String age;

    private final int size = 2; // 表示有两个字段可以迭代
    protected int modCount = 0;

    public User(String name, String age) {
        this.name = name;
        this.age = age;
    }

    @Override
    public Iterator<String> iterator() {
        return new Itr();
    }

    private class Itr implements Iterator<String> {
        private int cursor = 0;
        private int expectedModCount = modCount;

        @Override
        public boolean hasNext() {
            return cursor < size;
        }

        @Override
        public String next() {
            checkForComodification();
            if (cursor >= size) {
                throw new NoSuchElementException();
            }
            String result;
            if (cursor == 0) {
                result = User.this.name;
            } else { // cursor == 1
                result = User.this.age;
            }
            cursor++;
            return result;
        }

        private void checkForComodification() {
            if (modCount != expectedModCount) {
                throw new ConcurrentModificationException();
            }
        }
    }

    @Override
    public String toString() {
        return "User{" +
                "name='" + name + '\'' +
                ", age='" + age + '\'' +
                ", size=" + size +
                ", modCount=" + modCount +
                '}';
    }
}
