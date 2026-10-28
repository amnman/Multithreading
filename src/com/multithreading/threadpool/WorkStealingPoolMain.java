package com.multithreading.threadpool;

import java.util.Arrays;
import java.util.concurrent.*;

public class WorkStealingPoolMain {

    public static void main(String[] args){

        System.out.println("Available Processors: "+Runtime.getRuntime().availableProcessors());

        System.out.println("==============================RECURSIVE TASK EXAMPLE===============================");

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
        System.out.println("==============================RECURSIVE ACTION EXAMPLE===============================");
        double[] array = new double[16];
        System.out.println("Array Before Processing: "+ Arrays.toString(array));

        RecursiveActionExample recursiveActionExample = new RecursiveActionExample(array,0,array.length-1);

        try(ForkJoinPool pool=ForkJoinPool.commonPool()){
            ForkJoinTask<Void> result = pool.submit(recursiveActionExample);
            result.join();
        }
        System.out.println("Array After Processing: "+Arrays.toString(array));
    }
}
