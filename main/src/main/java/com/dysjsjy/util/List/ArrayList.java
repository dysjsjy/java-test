package com.dysjsjy.util.List;

import java.util.Arrays;

public class ArrayList<E> {
    private static final int DEFAULT_CAPACITY = 10;
    Object[] elements;
    int size;

    public ArrayList() {
        elements = new Object[DEFAULT_CAPACITY];
        size = 0;
    }

    public E get(int index) {
        checkIndex(index);
        return elementsData(index);
    }

    public void add(E e) {
        ensureCapacity(size + 1);
        elements[++size] = e;
    }

    public void add(int index, E element) {
        checkIndex(index);
        ensureCapacity(index);
        elements[index] = element;
    }

    public E remove(int index) {
        checkIndex(index);
        E oldValue = elementsData(index);
        int moveCount = size - index - 1;
        if (moveCount > 0) {
            System.arraycopy(elements, index + 1, elements, index, moveCount);
        }
        return oldValue;
    }

    public boolean contains(E e) {
        return indexOf(e) >= 0;
    }

    private void ensureCapacity(int minCapacity) {
        if (minCapacity > elements.length) {
            int newCapacity = elements.length + (elements.length >> 1);
            if (newCapacity < minCapacity) {
                newCapacity = minCapacity;
            }
            elements = Arrays.copyOf(elements, newCapacity);
        }
    }

    public int indexOf(E e) {
        if (e == null) {
            for (int i = 0; i < size; i++) {
                if (elements[i] == null) {
                    return i;
                }
            }
        } else {
            for (int i = 0; i < size; i++) {
                if (e.equals(elements[i])) {
                    return i;
                }
            }
        }
        return -1;
    }

    private void checkIndex(int index) {
        if (!(0 <= index && index < size)) {
            throw new IndexOutOfBoundsException("index: " + index + "size: " + size);
        }
    }

    @SuppressWarnings("unchecked")
    private E elementsData(int index) {
        return (E) elements[index];
    }

    public static void main(String[] args) {
        /*
            你能说说为什么扩容是 1.5 倍而不是 2 倍？
                略
            为什么使用 Object[] 而不是 E[]？
                因为Java不支持范型数组，Java的范型是类型擦除实现的，编译后范型信息会被擦除，
            如何设计一个线程安全的 ArrayList？
                List<E> list = Collections.synchronizedList(new ArrayList<>());
                List<E> list = new CopyOnWriteArrayList<>();
            如果要优化插入/删除性能你会考虑用什么数据结构？
                ArrayDeque（双端队列，性能更优）
                SkipList（如 ConcurrentSkipListMap）
                或在特定场景下自定义结构，如环形缓冲区。

         */

    }
}
