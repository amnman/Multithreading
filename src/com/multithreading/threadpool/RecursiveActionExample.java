package com.multithreading.threadpool;

import java.util.concurrent.RecursiveAction;

public class RecursiveActionExample extends RecursiveAction {
    private final double[] array;
    private final int start;
    private final int end;

    public RecursiveActionExample(double[] array, int start, int end) {
        this.array = array;
        this.start = start;
        this.end = end;
    }

    @Override
    protected void compute() {

        if((end-start)<=5){
            for(int i=start;i<=end;i++){
                array[i]= Math.sqrt(i+1);
            }
        }else{
            int mid = (start+end)/2;
            RecursiveActionExample task1 = new RecursiveActionExample(array,start,mid);
            RecursiveActionExample task2 = new RecursiveActionExample(array,mid+1,end);

            task1.fork();
            task2.fork();

            task1.join();
            task2.join();
        }
    }

    public double[] getArray(){
        return this.array;
    }
}
