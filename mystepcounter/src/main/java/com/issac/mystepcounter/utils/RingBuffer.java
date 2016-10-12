package com.issac.mystepcounter.utils;

import android.util.Log;

import java.util.Arrays;

/**
 * Created by Administrator on 2016/10/10.
 */

public class RingBuffer<T> {
    /**
     * 循环队列 （数组）默认大小
     */
    private final int DEFAULT_SIZE = 11;

    /**
     * (循环队列)数组的容量
     */
    public int capacity;

    /**
     * 数组：保存循环队列的元素
     */
    public Object[] elementData;

    /**
     * 队头(先见先出)
     */
    public int head = 0;

    /**
     * 队尾
     */
    public int tail = -1;

    /**
     * 以循环队列 默认大小创建空循环队列
     */
    public RingBuffer() {
        capacity = DEFAULT_SIZE;
        elementData = new Object[capacity];
    }

    /**
     * 以指定长度的数组来创建循环队列
     *
     * @param initSize
     */
    public RingBuffer(final int initSize) {
        capacity = initSize;
        elementData = new Object[capacity];
    }

    /**
     * 获取循环队列的大小(包含元素的个数)
     */
    public int size() {
        if (isEmpty()) {
            return 0;
        } else if (isFull()) {
            return capacity;
        } else {
            return tail + 1;
        }
    }

    /**
     * 插入队尾一个元素
     */
    public void add(final T element) {
        if (isEmpty()) {
            elementData[0] = element;
            tail++;
        } else if (isFull()) {
            elementData[head] = element;
            head++;
            tail++;
            head = head == capacity ? 0 : head;
            tail = tail == capacity ? 0 : tail;
        } else {
            tail++;
            elementData[tail] = element;
        }
    }

    public boolean isEmpty() {
        return tail==-1;
    }

    public boolean isFull() {
        return (head != 0 && head - tail == 1) || (head == 0 && tail == capacity - 1);
    }

    public void clear() {
        Arrays.fill(elementData, null);
        head = 0;
        tail = -1;
    }

    public boolean hasPeak(){
        if (isFull()){
            float value = (Float) elementData[(head+5)%11];
            int i=0;

            if(value>10) {
                while (i < 11) {
                    if (value < (Float) elementData[(head + i) % 11]) {
                        return false;
                    }
                    i++;
                }
                return true;
            }
        }
        return false;
    }

    /**
     * @return 取 循环队列里的值（先进的index=0）
     */
    public Object[] getQueue() {
        final Object[] elementDataSort = new Object[capacity];
        final Object[] elementDataCopy = elementData.clone();
        if (isEmpty()) {
        } else if (isFull()) {
            int indexMax = capacity;
            int indexSort = 0;
            for (int i = head; i < indexMax;) {
                elementDataSort[indexSort] = elementDataCopy[i];
                indexSort++;
                i++;
                if (i == capacity) {
                    i = 0;
                    indexMax = head;
                }
            }
        } else {
            // elementDataSort = elementDataCopy;//用这行代码代替下面的循环，在队列刚满时候会出错
            for (int i = 0; i < tail; i++) {
                elementDataSort[i] = elementDataCopy[i];
            }
        }
        return elementDataSort;
    }

    /**
     * 测试代码
     */
    public static void main(final String[] args) {
        final RingBuffer<Integer> queue = new RingBuffer<Integer>(10);
        for (int i = 0; i < 99; i++) {
            queue.add(i);
        }

        final Object[] queueArray = queue.getQueue();
        System.out.println("按入队列的先后顺序打印出来：");
        for (final Object o : queueArray) {
            System.out.println(o);
        }
        System.out.println("capacity: " + queue.capacity);
        System.out.println("size: " + queue.size());
        System.out.println("head index: " + queue.head);
        System.out.println("tail index: " + queue.tail);

    }
}
