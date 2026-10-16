package com.multithreading.monitorlock;

public class MonitorLockExample {

    public synchronized void task1(){
        try{
            System.out.println("Inside Task1...");
            Thread.sleep(10000);
            System.out.println("Task1 Completed!!...");
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
    public void task2(){
        System.out.println("Task2: Entering Synchronized!!");
        synchronized (this){
            System.out.println("Task2: Inside Synchronized");
        }
    }
    public void task3(){
        System.out.println("Task3: Executed....");
    }
}
