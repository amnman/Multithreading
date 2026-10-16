package com.multithreading.lockfree;

public class Main {
    public static void main(String[] args){
        AtomicCounter atomicCounter = new AtomicCounter();
        VolatileCounter volatileCounter = new VolatileCounter();

        Thread t1 = new Thread(()->{
           for(int i=0;i<100_000;i++){
               atomicCounter.increment();
           }
        });
        Thread t2 = new Thread(()->{
            for(int i=0;i<100_000;i++){
                atomicCounter.increment();
            }
        });

        Thread t3 = new Thread(()->{
            for(int i=0;i<100_000;i++){
                volatileCounter.increment();
            }
        });
        Thread t4 = new Thread(()->{
            for(int i=0;i<100_000;i++){
                volatileCounter.increment();
            }
        });

        t1.start();
        t2.start();
        t3.start();
        t4.start();

        try {
            t1.join();
            t2.join();
            t3.join();
            t4.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        System.out.println("Atomic Counter : "+atomicCounter.getCount());
        System.out.println("Volatile Counter: "+volatileCounter.getCount());
    }
}
