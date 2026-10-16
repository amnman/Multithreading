package com.multithreading.monitorlock;

public class AddCheckout {
    private boolean cartEmpty = true;

    public  void addItem(){
        System.out.println("Adding Item....");
        cartEmpty = false;
        notifyAll();
        System.out.println("Item added!!");
    }
    public synchronized void checkOutItem() {
        if(cartEmpty){
            System.out.println("Waiting for Item to be added.....");
            try{
                wait();
            }catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        System.out.println("Item Checkout Completed....");
        cartEmpty = true;
    }
}
