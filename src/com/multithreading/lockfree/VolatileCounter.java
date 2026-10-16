package com.multithreading.lockfree;


public class VolatileCounter {
    private volatile int counter = 0;

    public void increment(){
        counter+=1;
    }
    public int getCount(){
        return counter;
    }
}
