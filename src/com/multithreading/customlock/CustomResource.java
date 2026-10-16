package com.multithreading.customlock;

import java.util.concurrent.locks.StampedLock;

public class CustomResource {
    int a = 10;
    StampedLock lock = new StampedLock();
    public void producer(){
        long stamp = lock.tryOptimisticRead();
        System.out.println("[OptimisticLock]: OptimisticReadLock Acquired at: "+System.currentTimeMillis());
        try{
            Thread.sleep(1600);
            if(lock.validate(stamp)){
                a = 26;
                System.out.println("[OptimisticLock]: Current 'a' value: "+a);
            }else{
                System.out.println("[OptimisticLock]: Performing Rollback!!");
                a = 10;
                System.out.println("[OptimisticLock]: Rollback Completed.");
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void producerReadLock(){
        long stamp = lock.readLock();
        System.out.println("[ReadLock]: ReadLock Acquired at: "+System.currentTimeMillis());
        try{
            Thread.sleep(1600);
            if(lock.validate(stamp)){
                a = 26;
                System.out.println("[ReadLock]: Current 'a' value: "+a);
            }else{
                System.out.println("[ReadLock]: Performing Rollback!!");
                a = 10;
                System.out.println("[ReadLock]: Rollback Completed.");
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }finally {
            lock.unlock(stamp);
        }
    }

    public void consumer(){
        long stamp = lock.writeLock();
        System.out.println("[WriteLock]: WriteLock Acquired at:"+System.currentTimeMillis());
        try{
            System.out.println("[WriteLock]: Performing Update");
            a=5;
            System.out.println("[WriteLock]: Update Successful!! 'a' Value updated to : "+a);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }finally {
            lock.unlock(stamp);
            System.out.println("[WriteLock]: Lock Released!!");
        }
    }
}
