package com.multithreading.lockfree;

import java.util.concurrent.atomic.AtomicInteger;

public class AtomicCounter {
    private AtomicInteger counter = new AtomicInteger(0);

    public void increment(){
        counter.addAndGet(1);
    }
    public int getCount(){
        return counter.get();
    }
}
