package com.dysjsjy.util.List;

import java.util.Iterator;
import java.util.NoSuchElementException;

// ArrayList 手撕模板
public class MyArrayList<E> implements Iterable<E> {
    private Object[] elementData; // 存储元素的数组
    private int size; // 当前元素数量
    private static final int DEFAULT_CAPACITY = 10; // 默认初始容量

    // 构造方法
    public MyArrayList() {
        this.elementData = new Object[DEFAULT_CAPACITY];
        this.size = 0;
    }

    // 添加元素到末尾
    public boolean add(E e) {
        ensureCapacity(size);
        elementData[size] = e;
        size++;
        return true;
    }

    // 在指定索引添加元素
    public void add(int index, E element) {
        if (index < 0 || index >= size) {
            return;
        }

        ensureCapacity(size);

        // 1, 2, 3, 4, 5 -> 1, 2, 6, 3, 4, 5
        System.arraycopy(elementData, index, elementData, index + 1, size - index);
        elementData[index] = element;
        size++;
    }

    // 获取指定索引的元素
    public E get(int index) {
        if (index < 0 || index >= size) {
            return null;
        }

        E e = (E) elementData[index];

        return e;
    }

    // 删除指定索引的元素
    public E remove(int index) {
        if (index < 0 || index >= size) {
            return null;
        }

        E e = (E) elementData[index];

        // 1, 2, 3, 4, 5 -> 1, 2, 4, 5
        System.arraycopy(elementData, index + 1, elementData, index, size - index - 1);
        size--;
        elementData[size] = null;
        return e;
    }

    // 返回列表大小
    public int size() {
        return this.size;
    }

    // 扩容方法
    private void ensureCapacity(int minCapacity) {
        if (minCapacity == elementData.length) {
            Object[] newElementData = new Object[elementData.length * 2];
            // 1, 2, 3, 4, 5, 6
            System.arraycopy(elementData, 0, newElementData, 0, elementData.length);
            elementData = newElementData;
        }
    }

    public Iterator<E> iterator() {
        return new IteratorBuilder();
    }

    class IteratorBuilder implements Iterator<E> {

        int cursor;

        @Override
        public boolean hasNext() {
            return cursor != size;
        }

        @Override
        public E next() {
            if (cursor >= size) {
                throw new NoSuchElementException();
            }
            E e = (E) elementData[cursor];
            cursor++;
            return e;
        }
    }

    public static void main(String[] args) {
        MyArrayList<Integer> integerMyArrayList = new MyArrayList<>();
        for (int i = 0; i < 30; i++) {
            integerMyArrayList.add(i);
        }
        integerMyArrayList.remove(20);
        integerMyArrayList.remove(21);
        System.out.println(integerMyArrayList.size());
        for (int i : integerMyArrayList) {
            System.out.printf(i + ",");
        }
    }
}