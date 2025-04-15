package com.dysjsjy.util.Set;

public class MyHashSet<E> {
    private static final int DEFAULT_CAPACITY = 16;
    private Node<E>[] table;
    private int size;

    public MyHashSet() {
        table = new Node[DEFAULT_CAPACITY];
    }

    public boolean add(E key) {
        int index = indexFor(key);
        Node<E> current = table[index];

        while (current != null) {
            if (current.key.equals(key)) return false; // 已存在
            current = current.next;
        }

        Node<E> newNode = new Node<>(key);
        newNode.next = table[index];
        table[index] = newNode;
        size++;
        return true;
    }

    public boolean contains(E key) {
        int index = indexFor(key);
        Node<E> current = table[index];

        while (current != null) {
            if (current.key.equals(key)) return true;
            current = current.next;
        }
        return false;
    }

    public boolean remove(E key) {
        int index = indexFor(key);
        Node<E> current = table[index];
        Node<E> prev = null;

        while (current != null) {
            if (current.key.equals(key)) {
                if (prev == null) {
                    table[index] = current.next;
                } else {
                    prev.next = current.next;
                }
                size--;
                return true;
            }
            prev = current;
            current = current.next;
        }

        return false;
    }

    private int indexFor(E key) {
        return (key == null ? 0 : key.hashCode() & 0x7FFFFFFF) % table.length;
    }

    private static class Node<E> {
        E key;
        Node<E> next;

        Node(E key) {
            this.key = key;
        }
    }

    public int size() {
        return size;
    }
}