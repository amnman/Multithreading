package com.multithreading.monitorlock;

import java.util.PriorityQueue;
import java.util.Queue;

public class ProducerConsumer {
    private final Queue<Integer> queue;
    private final int bufferSize;
    public ProducerConsumer(int bufferSize){
        this.queue = new PriorityQueue<>(bufferSize);
        this.bufferSize = bufferSize;
    }
    public synchronized void addElement(int element){
        if(queue.size()==bufferSize){
            System.out.println(Thread.currentThread().getName()+" : Buffer Queue is full!!");
        }else{
            queue.add(element);
        }
    }
    public synchronized void getElement(){
        if(queue.isEmpty()){
            System.out.println(Thread.currentThread().getName()+" : Queue is Empty!!");
        }else{
            System.out.println(Thread.currentThread().getName()+" : Queue Element: "+queue.poll());
        }
    }
    public synchronized void produce(int element){
        while(queue.size()==bufferSize){
            try {
                System.out.println("Queue Full!! Waiting to consume messages...");
                wait();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        queue.add(element);
        System.out.println("Element Added!!");
        notifyAll();
    }
    public synchronized void consume(){
        while(queue.isEmpty()){
            try {
                System.out.println("Queue Empty!! Waiting to produce messages...");
                wait();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        System.out.println("Element removed: "+queue.poll());
        notifyAll();
    }


}
