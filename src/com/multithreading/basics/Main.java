package com.multithreading.basics;

import com.multithreading.monitorlock.AddCheckout;
import com.multithreading.monitorlock.MonitorLockExample;
import com.multithreading.monitorlock.ProducerConsumer;

public class Main {
    public static void main(String[] args){

    }
    /**
     * Experimenting the Custom thread creation using Thread class and runnable
     * Method: test01()
     * **/

    public static void test01(){
        CustomThread thread1 = new CustomThread();
        thread1.start();
        System.out.println("In com.multithreading.customlock.Main Method before runnable: "+Thread.currentThread().getName());
        Thread thread2 = new Thread(new CustomRunnable(),"com.multithreading.basics.CustomRunnable");
        thread2.start();
        System.out.println("In com.multithreading.customlock.Main Method after runnable: "+Thread.currentThread().getName());
    }

    /**
     * Experimenting Functioning of the Daemon thread
     * Method: test06
     * **/

    public static void test02(){
        Thread t1 = new Thread(()->{
            try{
                System.out.println("Thread t1 going to Sleep...");
                Thread.sleep(2500);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            System.out.println("Thread t1 Execution completed!!");
        });
        t1.setDaemon(true);
        t1.start();
        System.out.println("Program Execution Completed!!!");
    }
}
