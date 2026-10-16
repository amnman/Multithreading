package com.multithreading.customlock;

import java.util.concurrent.Semaphore;

public class SemaphoreResource {
    Semaphore lock = new Semaphore(2);

    public void locker(){
        try {
            lock.acquire();
            System.out.println("[Semaphore Lock]: Lock acquired by Thread: "+Thread.currentThread().getName()+" at "+System.currentTimeMillis());
            Thread.sleep(1500);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }finally {
            lock.release();
            System.out.println("[Semaphore Lock]: Lock released by Thread: "+Thread.currentThread().getName()+" at "+System.currentTimeMillis());
        }
    }
}
