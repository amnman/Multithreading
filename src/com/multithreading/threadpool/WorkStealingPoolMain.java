package com.multithreading.threadpool;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.Future;

public class WorkStealingPoolMain {

    public static void main(String[] args){

        System.out.println("Available Processors: "+Runtime.getRuntime().availableProcessors());

        /**
         * CommonPool method in the forkJoinPool creates a thread pool with the pool size equal to the AvailableProcessors-1
         * **/

        try (ForkJoinPool forkJoinPool = ForkJoinPool.commonPool()) {

            /**
             * Calling the Submit method adds the task to the submission queue
             * **/
            Future<Integer> result = forkJoinPool.submit(new RecursiveTaskExample(1,26));

            System.out.println("Total Sum: "+result.get());

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
