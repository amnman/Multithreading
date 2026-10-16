package com.multithreading.customlock;

import java.util.PriorityQueue;
import java.util.Queue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class ProducerReceiver {

    Queue<String> queue = new PriorityQueue<>(10);
    boolean isAvailable = false;
    ReentrantLock lock = new ReentrantLock();
    Condition condition = lock.newCondition();
    int counter = 0;
    public void producer(){
        lock.lock();
        isAvailable = queue.isEmpty();
        System.out.println("[Producer]: Lock Acquired!!");
        try{
            if(isAvailable){
                System.out.println("[Producer]: Queue Empty. Adding Message..");
                queue.add("Message - "+counter);
                System.out.println("[Producer]: Message Added!");
                counter++;
                condition.signal();
            }
            System.out.println("[Producer]: Waiting...");
            condition.await();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        finally {
            lock.unlock();
            System.out.println("[Producer]: Lock Released!!");
        }
    }
    public void consumer(){
        lock.lock();
        isAvailable = queue.isEmpty();
        System.out.println("[Consumer]: Lock Acquired!!");
        try{
            if(isAvailable){
                System.out.println("[Consumer]: Waiting for Message...");
                Thread.sleep(1500);
                condition.await();
            }
            String s = queue.poll();
            System.out.println("[Consumer]: Message Consumed -> Message: "+s);
            condition.signal();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }finally {
            lock.unlock();
            System.out.println("[Consumer]: Lock Released!!");
        }
    }
}
