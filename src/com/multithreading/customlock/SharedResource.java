package com.multithreading.customlock;

import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantLock;

public class SharedResource {

    public synchronized void process(){
        try{
            System.out.println("Process() Method executing Sleep for Thread: "+Thread.currentThread().getName());
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        System.out.println("Execution Completed for Thread: "+Thread.currentThread().getName());
    }

    public void process(ReentrantLock lock){
        try{
            lock.lock();
            System.out.println("Lock Acquired by Thread: "+Thread.currentThread().getName()+" at "+System.currentTimeMillis());
            Thread.sleep(4000);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }finally {
            lock.unlock();
            System.out.println("Lock Released by Thread: "+Thread.currentThread().getName()+" at "+System.currentTimeMillis());
        }
    }

    public void produce(ReadWriteLock lock){
        try {
            lock.readLock().lock();
            System.out.println("ReadLock Acquired by Thread: "+Thread.currentThread().getName()+" at "+System.currentTimeMillis());
            Thread.sleep(4000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }finally {
            lock.readLock().unlock();
            System.out.println("ReadLock Released by Thread: "+Thread.currentThread().getName()+" at "+System.currentTimeMillis());
        }
    }
    public void consume(ReadWriteLock lock){
        try{
            lock.writeLock().lock();
            System.out.println("WriteLock Acquired by Thread: "+Thread.currentThread().getName()+" at "+System.currentTimeMillis());
            Thread.sleep(4000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }finally {
            lock.writeLock().unlock();
            System.out.println("WriteLock Released by Thread: "+Thread.currentThread().getName()+" at "+System.currentTimeMillis());
        }
    }
}
