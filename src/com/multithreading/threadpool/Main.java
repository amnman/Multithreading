package com.multithreading.threadpool;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class Main {

    public static void main(String[] args){

        threadPoolExecutor05();
    }

    /**
     * threadPoolExecutor01 method explains the working of a ThreadPoolExecutor
     *    corePoolSize: Represents the min number of threads get created and is kept idle
     *    maximumPoolSize: Represents the max number of threads that can get created overall if all the existing threads are assigned
     *                     and the queue is also full.
     *    keepAliveTime: Time after which the even Idle threads get terminated.
     *    workQueue: Queue used to store the task assigned to thread pool.
     *    threadFactory: Interface which provides the threads to thread pool service
     *    handler: interface which determines how to handle the rejected tasks when the threads are fully occupied and queue is full.
     *    When a task is assigned to thread pool, it checks if there are vacant threads available. If found, assigns the task to the thread else adds to the queue.
     *    Now, if the queue is full, then new additional threads are created based on the max pool size. Now if the queue is full and all the threads are created,
     *    any new task trying to get assigned will be rejected.
     * **/
    public static void threadPoolExecutor01(){
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(2,4,10, TimeUnit.MINUTES,
                new ArrayBlockingQueue<>(2),new CustomThreadFactory(),new CustomExecutionHandler());
        threadPoolExecutor.allowCoreThreadTimeOut(true);
        for(int i=1;i<=10;i++){
            int j = i;
            threadPoolExecutor.submit(()->{
                try{
                    Thread.sleep(5000);
                }catch (InterruptedException e){
                    System.out.println(e);
                }
                System.out.println("Task:"+j+" - executed by "+Thread.currentThread().getName());
            });
        }
        threadPoolExecutor.shutdown();
    }

    /**
     * Future is the interface, which represents the result of an async task.
     * get(Timeout) - Main thread Waits for the secondary thread to return the future object response within the specified time else throws a TimeoutException
     * get() - Main thread waits for the secondary thread until the response is received.
     * isDone() - returns true if the task is completed or if its cancelled
     * isCancelled() - returns true if the task is cancelled.
     * The main thread execution doesn't wait for the secondary thread to obtain the response. Only if there is a get() method,
     * then main thread waits for the execution to return a response
     * **/
    public static void threadPoolExecutor02(){
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(1,1,1,TimeUnit.HOURS,
                new ArrayBlockingQueue<>(10),Executors.defaultThreadFactory(),new ThreadPoolExecutor.AbortPolicy());

            Future<?> response = threadPoolExecutor.submit(()->{
                try{
                    Thread.sleep(7000);
                }catch (InterruptedException e){
                }
                System.out.println("Task 1 Executed..");
            });

            System.out.println("Is task 1 completed : "+response.isDone());

            try{
                response.get(1,TimeUnit.SECONDS);
            }catch (TimeoutException e){
                System.out.println("Timeout Exception Occurred...");
            } catch (ExecutionException | InterruptedException e) {
                throw new RuntimeException(e);
            }

            try{
                response.get();
            } catch (ExecutionException | InterruptedException e) {
                throw new RuntimeException(e);
            }
            System.out.println("Task 1 Execution completed successfully!!");
            System.out.println("Task 1 Execution cancelled: "+response.isCancelled());

        Future<?> response01 = threadPoolExecutor.submit(()->{
            try{
                Thread.sleep(7000);
            }catch (InterruptedException e){
            }
            System.out.println("Task 2 Executed..");
        });

        boolean cancelled = response01.cancel(true);
        System.out.println("Task 2 Execution is cancel initiated: "+cancelled);
        System.out.println("Task 2 Execution is cancelled: "+response01.isCancelled());
        System.out.println("Task 2 Execution is completed: "+response01.isDone());

        threadPoolExecutor.shutdown();
    }

    /**
     * Using the variant of submit function which accepts a runnable and returns an output as well.
     * <T> Future<T> submit(Runnable Task, T)
     * Using get() ensures that the execution waits till the result is obtained from the future object.
     * **/
    public static void threadPoolExecutor03(){
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(1,1,10,TimeUnit.MINUTES,new ArrayBlockingQueue<>(10),Executors.defaultThreadFactory(),new ThreadPoolExecutor.AbortPolicy());
        List<Integer> list = new ArrayList<>();
        CustomRunnable runnable = new CustomRunnable(list);
        List<List<Integer>> result = new ArrayList<>();
        Future<List<Integer>> response = threadPoolExecutor.submit(runnable,list);
        try{
            result.add(response.get());
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
        System.out.println("Result: "+ result);

        threadPoolExecutor.shutdown();
    }
    /**
     * Using the variant of submit() method which accepts a callable.
     * Returns a Object or value based on the callable return type.
     * **/
    public static void threadPoolExecutor04(){
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(1,1,10,TimeUnit.MINUTES,new ArrayBlockingQueue<>(10),Executors.defaultThreadFactory(),new ThreadPoolExecutor.AbortPolicy());
        Future<List<Integer>> response = threadPoolExecutor.submit(()->{
            List<Integer> list = new ArrayList<>();
            list.add(26);
            try{
                Thread.sleep(1000);
            }catch (InterruptedException e){
                throw new Exception(e);
            }
            return list;
        });
        try{
            long startTime = System.currentTimeMillis();
            List<Integer> resp = response.get();
            long endTime = System.currentTimeMillis();
            System.out.println("List: "+resp);
            System.out.println("Result received in : "+(endTime-startTime)+" ms");
        }catch (InterruptedException | ExecutionException e){
            throw new RuntimeException(e);
        }
        threadPoolExecutor.shutdown();
    }

    /**
     * Using the thenApply and thenApplyAsync method for creating a Chain of operations.
     * This methods are used to perform operations on the output obtained from the supplyAsync method.
     * thenApply runs on the same thread as the one used by the earlier function whereas thenApplyAsync uses different thread unless the executor is provided.
     * **/
    public  static void threadPoolExecutor05(){
        try {
            ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(1, 1, 10, TimeUnit.MINUTES, new ArrayBlockingQueue<>(10), Executors.defaultThreadFactory(), new ThreadPoolExecutor.AbortPolicy());

            CompletableFuture<String> resp01 = CompletableFuture.supplyAsync(() -> {
                System.out.println("Inside SupplyAsync Method - Thread : "+Thread.currentThread().getName());
                return "Supplier Output";
            }, threadPoolExecutor).thenApply((String resp)->{
                System.out.println("Inside thenApply Method chain - Thread : "+Thread.currentThread().getName());
                return resp.concat(" enhanced01!");
            });

            System.out.println("Response from CompletableFuture resp01: "+resp01.get());
            CompletableFuture<String> resp02 = resp01.thenApply((String resp)->{
                System.out.println("Inside thenApply Method - Thread : "+Thread.currentThread().getName());
                return resp.concat(" enhanced02!");
            });
            System.out.println("Response from CompletableFuture resp02: "+resp02.get());
            CompletableFuture<String> respFinal = resp02.thenApplyAsync((String resp)->{
                System.out.println("Inside thenApplyAsync 01 Method - Thread : "+Thread.currentThread().getName());
                return resp.concat(" enhanced03!");
            },threadPoolExecutor).thenApplyAsync((String resp)->{
                    System.out.println("Inside thenApplyAsync 02 Method - Thread : " + Thread.currentThread().getName());
                    return resp.concat(" enhanced04!");
            }).thenApplyAsync((String resp)->{
                System.out.println("Inside thenApplyAsync 02 Method - Thread : "+Thread.currentThread().getName());
                return resp.concat(" enhanced05!");
            });

            System.out.println("Final Response: "+respFinal.get());

            /**
             * Async behaviour of the applyAsync method
             * **/

            System.out.println("======================================");

            CompletableFuture<String> asyncResp01 = CompletableFuture.supplyAsync(()->{
                try {
                    Thread.sleep(10000);
                    return "Async Response 01";
                }catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }).thenApplyAsync((String resp)->{
                System.out.println("Inside the ApplyAsync 01");
                return resp.concat(" - Received");
            });

            CompletableFuture<String> asyncResp02 = CompletableFuture.supplyAsync(()->{
                    return "Async Response 02";
            }).thenApplyAsync((String resp)->{
                System.out.println("Inside the ApplyAsync 02");
                return resp.concat(" - Received");
            });

            System.out.println("Async 01: "+asyncResp01.get(5,TimeUnit.MINUTES));
            System.out.println("Async 02: "+asyncResp02.get());

            threadPoolExecutor.shutdown();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    /**
     *
     * **/
    public static void threadPoolExecutor06(){
        try {
            ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(1, 1, 10, TimeUnit.MINUTES, new ArrayBlockingQueue<>(10), Executors.defaultThreadFactory(), new ThreadPoolExecutor.AbortPolicy());
            CompletableFuture<String> response = CompletableFuture.supplyAsync(()->{
                System.out.println("Thread Supplier: "+Thread.currentThread().getName());
                return "Thread Pool Executors";
            },threadPoolExecutor).thenCompose((String resp)->{
                System.out.println("Thread: "+Thread.currentThread().getName());
                return CompletableFuture.supplyAsync(()-> resp + " thenCompose ");
            }).thenComposeAsync((String resp)->{
                System.out.println("Thread Async 01: "+Thread.currentThread().getName());
                return CompletableFuture.supplyAsync(()-> resp + " thenComposeAsync01 ");
            }).thenComposeAsync((String resp)->{
                System.out.println("Thread Async 02: "+Thread.currentThread().getName());
                return CompletableFuture.supplyAsync(()-> resp + " thenComposeAsync02 ");
            },threadPoolExecutor);
            System.out.println("Response: "+response.get());

            threadPoolExecutor.shutdown();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}



class CustomThreadFactory implements ThreadFactory{

    @Override
    public Thread newThread(Runnable r) {
        Thread th = new Thread(r);
        th.setPriority(Thread.NORM_PRIORITY);
        th.setDaemon(false);
        return th;
    }

}

class CustomExecutionHandler implements RejectedExecutionHandler{

    @Override
    public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
        System.out.println("Task rejected - "+executor.toString());
    }
}