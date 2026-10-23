package com.multithreading.threadpool;

import java.util.ArrayList;
import java.util.List;

public class CustomRunnable implements Runnable{

    private List<Integer> list;

    public CustomRunnable(List<Integer> list){
        this.list=list;
    }

    @Override
    public void run() {
        list.add(26);
    }
}
