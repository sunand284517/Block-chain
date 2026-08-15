package com.example.myapplication;

public class SensorCircularBuffer {
    private final float[] buffer;
    private int head = 0;
    private int size = 0;
    private final int capacity;

    public SensorCircularBuffer(int capacity) {
        this.capacity = capacity;
        this.buffer = new float[capacity];
    }

    public synchronized void add(float value) {
        buffer[head] = value;
        head = (head + 1) % capacity;
        if (size < capacity) {
            size++;
        }
    }

    public synchronized float[] getData() {
        float[] orderedData = new float[size];
        for (int i = 0; i < size; i++) {
            orderedData[i] = buffer[(head + i) % size];
        }
        return orderedData;
    }
}