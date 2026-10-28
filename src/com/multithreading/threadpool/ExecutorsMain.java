package com.multithreading.threadpool;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ExecutorsMain {

    public static void main(String[] args){
        /**
         * FixedThreadPool ensures that the number of threads allocated for a Thread Pool is fixed and determined based on the nThreads parameter
         * **/
        ExecutorService executor01 = Executors.newFixedThreadPool(5);

        /**
         * CachedThreadPool creates threads based on requirement at the instant & keeps them cached for 60 seconds.
         * After the idle time the thread is destroyed if no task is available for thread.
         * **/
        ExecutorService executor02 = Executors.newCachedThreadPool();

        /**
         * SingleThreadPool creates a thread pool with a single thread, the execution runs similar to that of a sequential execution of statements with no concurrency.
         * **/
        ExecutorService executor03 = Executors.newSingleThreadExecutor();

        System.out.println("========== FIXED THREAD POOL ==========");

        for(int i=0;i<10;i++){
            System.out.println("=============");
            System.out.println("Fixed Thread Pool Task - "+i);
            System.out.println("=============");
            executor01.submit(()->{
                try{
                    Thread.sleep(5000);
                    System.out.println("Fixed Thread Pool Executing Thread: "+Thread.currentThread().getName());
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            });
        }

        System.out.println("========== CACHED THREAD POOL ==========");

        for(int i=0;i<10;i++){
            System.out.println("=============");
            System.out.println("Cached Thread Pool Task - "+i);
            System.out.println("=============");
            executor02.submit(()->{
                try{
                    Thread.sleep(5000);
                    System.out.println("Cached Thread Pool Executing Thread: "+Thread.currentThread().getName());
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            });
        }

        System.out.println("========== SINGLE THREAD POOL ==========");

        for(int i=0;i<10;i++){
            System.out.println("=============");
            System.out.println("Single Thread Pool Task - "+i);
            System.out.println("=============");
            executor03.submit(()->{
                try{
                    Thread.sleep(5000);
                    System.out.println("Single Thread Pool Executing Thread: "+Thread.currentThread().getName());
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            });
        }

        executor01.shutdown();
        executor02.shutdown();
        executor03.shutdown();
    }
}
