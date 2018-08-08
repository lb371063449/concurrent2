package com.rinbo.concurrent.chapter3;

import java.util.Random;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class ReaderWriterLockDemo {

    public Lock lock = new ReentrantLock();
    //构建读写锁
    public ReentrantReadWriteLock readWriteLock = new ReentrantReadWriteLock();
    public Lock readLock = readWriteLock.readLock();
    public Lock WritLock = readWriteLock.writeLock();

    private int value;

    public void read(Lock lock) {
        try {
            lock.lock();
            Thread.sleep(1000);
            System.out.println(value);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }

    public void write(Lock lock,int value) {
        try {
            lock.lock();
            Thread.sleep(1000);
            this.value = value;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }finally {
            lock.unlock();
        }
    }

    public static void main(String[] args) {
        ReaderWriterLockDemo demo = new ReaderWriterLockDemo();
        Runnable write = new Runnable() {
            @Override
            public void run() {
                demo.write(demo.WritLock,new Random().nextInt());
            }
        };

        Runnable read = new Runnable() {
            @Override
            public void run() {
                demo.read(demo.readLock);
            }
        };
        for (int i=0;i<2;i++) {
            new Thread(write).start();
        }
        for (int i=0;i<18;i++) {
            new Thread(read).start();
        }
    }

}
