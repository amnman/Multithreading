package com.multithreading.threadpool;

import java.util.concurrent.RecursiveTask;

public class RecursiveTaskExample extends RecursiveTask<Integer> {

    int start;
    int end;

    RecursiveTaskExample(int start,int end){
        this.start=start;
        this.end=end;
    }

    @Override
    protected Integer compute() {

        if(end-start<=4){
            System.out.println("[If-Thread]:"+Thread.currentThread().getName()+" | START: "+start+", END: "+end);
            int sum = 0;
            for(int i=start;i<=end;i++){
                sum+=i;
            }
            return sum;
        }else{
            System.out.println("[Else-Thread]:"+Thread.currentThread().getName()+" | START: "+start+", END: "+end);
            int mid = (start+end)/2;
            RecursiveTaskExample task1 = new RecursiveTaskExample(start,mid);
            RecursiveTaskExample task2 = new RecursiveTaskExample(mid+1,end);

            /**
             * Invoking the Fork method adds the task to the THREADS dequeue or the work-stealing queue
             * **/
            task1.fork();
            task2.fork();

            int leftTask = task1.join();
            int rightTask = task2.join();

            return leftTask+rightTask;
        }
    }
}
