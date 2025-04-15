package com.dysjsjy.util.Map.hashmap;


import java.util.Objects;


// 给每个桶设置了一把锁，锁在头节点上
public class MyConcurrentHashMap<K, V> {

    private static final int DEFAULT_CAPACITY = 16;

    private final Node<K, V>[] table;

    @SuppressWarnings("unchecked")
    public MyConcurrentHashMap() {
        this.table = (Node<K, V>[]) new Node[DEFAULT_CAPACITY];
    }

    static class Node<K, V> {
        final K key;
        volatile V value;
        Node<K, V> next;

        Node(K key, V value) {
            this.key = key;
            this.value = value;
        }
    }

    private int hash(Object key) {
        int h = key.hashCode();
        // 高低位混合扰动函数，模仿 HashMap
        return (h ^ (h >>> 16)) & (table.length - 1);
    }

    public V get(K key) {
        int index = hash(key);
        Node<K, V> node = table[index];
        while (node != null) {
            if (Objects.equals(node.key, key)) {
                return node.value; // volatile 保证可见性
            }
            node = node.next;
        }
        return null;
    }

    public void put(K key, V value) {
        int index = hash(key);
        synchronized (getLock(index)) {
            Node<K, V> head = table[index];
            Node<K, V> node = head;

            while (node != null) {
                if (Objects.equals(node.key, key)) {
                    node.value = value;
                    return;
                }
                node = node.next;
            }

            Node<K, V> newNode = new Node<>(key, value);
            newNode.next = head;
            table[index] = newNode;
        }
    }

    private final Object[] locks = new Object[DEFAULT_CAPACITY];

    {
        // 初始化锁数组
        for (int i = 0; i < locks.length; i++) {
            locks[i] = new Object();
        }
    }

    private Object getLock(int index) {
        return locks[index];
    }

    public void printAll() {
        for (int i = 0; i < table.length; i++) {
            Node<K, V> node = table[i];
            while (node != null) {
                System.out.println("[" + node.key + " -> " + node.value + "]");
                node = node.next;
            }
        }
    }
}

