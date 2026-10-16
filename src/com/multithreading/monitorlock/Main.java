package com.multithreading.monitorlock;

public class Main {
    public static void main(String[] args){

    }
    /**
     * Experimenting Monitor Locks while synchronized is in place.
     * Method: test02()
     * **/

    public static void test01(){
        MonitorLockExample lockExample = new MonitorLockExample();
        Thread thread1 = new Thread(lockExample::task1);
        Thread thread2 = new Thread(lockExample::task2);
        Thread thread3 = new Thread(lockExample::task3);

        thread2.start();
        thread1.start();
        thread3.start();
    }

    /**
     * Experimenting Shared resource notifyAll() - wait() method working
     * Method: test02()
     * **/

    public static void test02(){
        AddCheckout addCheckout = new AddCheckout();
        Thread addThread = new Thread(()->{
            try{
                System.out.println("Waiting for Add Service....");
                Thread.sleep(1000);
            }catch (InterruptedException e){
                throw new RuntimeException(e);
            }
            addCheckout.addItem();
        });
        Thread checkoutThread = new Thread(addCheckout::checkOutItem);
        checkoutThread.start();
        addThread.start();

        try{
            checkoutThread.join();
            addThread.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        System.out.println("Add-Checkout Completed.....");
    }



    /**
     * Experimenting the Producer-Consumer Example
     * Method: test04
     * **/

    public static void test04(){
        ProducerConsumer producerConsumer = new ProducerConsumer(15);
        Thread producer = new Thread(()->{
            for(int i=0;i<=15;i++){
                try{
                    System.out.println(Thread.currentThread().getName()+" : Producer Thread Sleeping...");
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                producerConsumer.addElement(i);
                System.out.println(Thread.currentThread().getName()+" : Element added!!");
            }
        });
        Thread consumer = new Thread(()->{
            for(int i =0;i<=15;i++){
                try{
                    System.out.println(Thread.currentThread().getName()+" : Consumer Thread Sleeping...");
                    Thread.sleep(1500);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                producerConsumer.getElement();
                System.out.println(Thread.currentThread().getName()+" : Element removed!!");
            }
        });
        producer.start();
        consumer.start();
    }

    /**
     * Experimenting the Producer-Consumer Example with Wait() - Notify()
     * Method: test05
     * **/

    public static void test05(){
        ProducerConsumer producerConsumer = new ProducerConsumer(5);
        Thread producer = new Thread(()->{
            for(int i=0;i<5;i++){
                producerConsumer.produce(i);
                System.out.println("Element Added: "+i);
            }
        });
        Thread consumer = new Thread(()->{
            for(int i=0;i<=5;i++){
                producerConsumer.consume();
                System.out.println("Element Removed!!");
            }
        });
        producer.start();
        consumer.start();
    }
}
