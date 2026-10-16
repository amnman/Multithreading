package com.multithreading.basics;

public class CustomRunnable implements Runnable{
    @Override
    public void run() {
        System.out.println("com.multithreading.basics.CustomRunnable Called...");
        System.out.println("Inside Runnable: "+Thread.currentThread().getName());
    }
}
