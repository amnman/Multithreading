package com.multithreading.customlock;

import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {
        test06();
    }

    /**
     * Experimenting Synchronized resource with two separate objects passed to two threads to understand Monitor
     * locking is performed on Object
     * Method: test07
     * **/

    public static void test01(){
        SharedResource resource1 = new SharedResource();
        SharedResource resource2 = new SharedResource();
        Thread t1 = new Thread(resource1::process);
        Thread t2 = new Thread(resource2::process);
        t1.start();
        t2.start();
    }

    /**
     * Experimenting Custom Lock using ReentrantLock(). Locking resource when different objects are being used.
     * Method: test08
     * **/

    public static void test02(){
        ReentrantLock lock = new ReentrantLock();
        SharedResource resource1 = new SharedResource();
        SharedResource resource2 = new SharedResource();
        Thread t1 = new Thread(()->{
            resource1.process(lock);
        });
        Thread t2 = new Thread(()->{
            resource2.process(lock);
        });
        t1.start();
        t2.start();
    }

    /**
     * Experimenting Custom Lock using ReadWriteLock(). Locking resource when different objects are being used.
     * ReadLock provides a shared lock and read operations can be performed. WriteLock provides an exclusive Lock and write operations can be performed.
     * Method: test09
     * **/

    public static void test03(){
        ReadWriteLock lock = new ReentrantReadWriteLock();
        SharedResource resource1 = new SharedResource();
        SharedResource resource2 = new SharedResource();
        SharedResource resource3 = new SharedResource();
        SharedResource resource4 = new SharedResource();
        Thread t1 = new Thread(()->{
            resource1.produce(lock);
        });
        Thread t2 = new Thread(()->{
            resource2.produce(lock);
        });
        Thread t3 = new Thread(()->{
            resource3.consume(lock);
        });
        Thread t4 = new Thread(()->{
            resource4.consume(lock);
        });
        t1.start();
        t2.start();
        t3.start();
        t4.start();
    }

    /**
     * Experimenting StampedLock with OptimisticRead and Write Locks.
     * OptimisticRead saves the Lock version and when ever there are changes made then a read is performed to update the version
     * Method: test10
     *  **/

    public static void test04(){
        CustomResource resource = new CustomResource();
        Thread t1 = new Thread(resource::producer);
        Thread t2 = new Thread(resource::consumer);
        Thread t3 = new Thread(resource::producer);
        t1.start();
        t2.start();
        t3.start();
    }

    /**
     * Experimenting Semaphore Locks
     * **/

    public static void test05(){
        SemaphoreResource resource = new SemaphoreResource();

        Thread t1 = new Thread(resource::locker);
        Thread t2 = new Thread(resource::locker);
        Thread t3 = new Thread(resource::locker);
        Thread t4 = new Thread(resource::locker);

        t1.start();
        t2.start();
        t3.start();
        t4.start();
    }

    /**
     * Experimenting the Await and Signal method for Explicit Locks
     * **/
    public static void test06(){
        ProducerReceiver resource = new ProducerReceiver();
        Thread producer = new Thread(()->{
            for(int i=0;i<5;i++){
                resource.producer();
            }
        });
        Thread consumer = new Thread(()->{
            for(int i=0;i<5;i++){
                resource.consumer();
            }
        });
        consumer.start();
        producer.start();
    }
}