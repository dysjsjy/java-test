package com.dysjsjy.util.List;


import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;

// 真不是随便就能写出来的！
// LinkedList 手撕模板
class MyLinkedList<E> implements Iterable<E> {

    private class Node<E> {
        E data;
        Node<E> prev;
        Node<E> next;

        Node(E data) {
            this.data = data;
        }

        Node(Node<E> prev, E data, Node<E> next) {
            this.prev = prev;
            this.data = data;
            this.next = next;
        }
    }

    private Node<E> head; // 头节点
    private Node<E> tail; // 尾节点
    private int size; // 元素数量

    // 添加元素到末尾
    // a -> b
    public void add(E e) {
        Node<E> node = new Node<E>(tail, e, null);
        if (tail != null) {
            tail.next = node;
        } else {
            head = node;
        }

        tail = node;
        size++;
    }

    // 在指定索引添加元素
    public void add(int index, E element) {
        // TODO: 实现索引检查和节点插入
        // 这里有个问题如果是在尾部插入的话用findNode插入不了，
        if (index < 0 || index > size) {
            throw new NoSuchElementException();
        }

        // a -> b -> c
        if (index == size) {
            add(element);
            return;
        }

        Node<E> indexNode = findNode(index);

        Node<E> prev = indexNode.prev;
        Node<E> node = new Node<E>(prev, element, indexNode);
        if (prev == null) {
            head = node;
        } else {
            prev.next = node;
        }
        indexNode.prev = node;
        size++;
    }

    protected Node<E> findNode(int index) {
        if (index < 0 || index >= size) {
            throw new NoSuchElementException();
        }

        Node<E> node = null;

        if (index < size / 2) {
            node = head;
            // 1, 2, 3, 4, 5
            for (int i = 0; i < index; i++) {
                node = node.next;
            }
        } else {
            node = tail;
            // 1, 2, 3, 4, 5
            for (int i = size - 1; i > index; i--) {
                node = node.prev;
            }
        }

        return node;
    }

    // 获取指定索引的元素
    public E get(int index) {
        // TODO: 实现索引检查和节点查找
        Node<E> node = findNode(index);
        return node.data;
    }

    // 删除指定索引的元素
    public boolean remove(E element) {
        Node<E> node = head;
        while (node != null) {
            if (Objects.equals(node.data, element)) {
                removeNode(node);
                return true;
            }
            node = node.next;
        }
        return false;
    }

    public E removeNode(Node<E> node) {
        Node<E> prev = node.prev;
        Node<E> next = node.next;

        if (prev == null) {
            head = next;
        } else {
            prev.next = next;
        }
        if (next == null) {
            tail = prev;
        } else {
            next.prev = prev;
        }

        node.prev = null;
        node.next = null;
        size--;

        return node.data;
    }

    public E set(int index, E element) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException();
        }
        Node<E> node = findNode(index);
        E oldValue = node.data;
        node.data = element;
        return oldValue;
    }


    // 返回列表大小
    public int size() {
        return size;
    }

    @Override
    public Iterator<E> iterator() {
        return new IteratorBulider();
    }

    class IteratorBulider implements Iterator<E> {

        Node<E> node = head;

        @Override
        public boolean hasNext() {
            return node != null;
        }

        @Override
        public E next() {
            if (node == null) {
                throw new NoSuchElementException();
            }
            E data = node.data;
            node = node.next;
            return data;
        }
    }

    public static void main(String[] args) {
        MyLinkedList<Integer> list = new MyLinkedList<>();
        for (int i = 0; i < 30; i++) {
            list.add(i);
        }
        list.remove(20);
        list.remove(21);
        System.out.println(list.size());
        for (int i = 0; i < 28; i++) {
            System.out.println(list.get(i));
        }
    }
}