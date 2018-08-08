package com.rinbo.concurrent.condition;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ConditionDemo {
    private static Lock lock = new ReentrantLock();
    private static Condition condition = lock.newCondition();
    public static void main(String[] args) {
        Thread[] wthreads = new Thread[5];
        for (int i=1;i<=4;i++) {
            wthreads[i-1] = new Thread(new Runnable(){

                @Override
                public void run() {
                    lock.lock();
                    System.out.println(Thread.currentThread().getName()+"正在运行。。。。");
                    try {
                        Thread.sleep(100);
                        System.out.println(Thread.currentThread().getName()+"停止运行，等待一个signal");
                        condition.await();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    System.out.println(Thread.currentThread().getName()+"获得一个signal，继续执行");
                    lock.unlock();
                }

            },"w" + i);
        }
        for (int i=1;i<=4;i++) {
            wthreads[i-1].start();
        }

        try {
            Thread.sleep(1000);//保证线程1先执行，否则线程1将一直等待signal信号
        } catch (InterruptedException e1) {
            e1.printStackTrace();
        }
        for (int i=1;i<=4;i++) {
            int finalI = i;
            new Thread(new Runnable(){

                @Override
                public void run() {
                    lock.lock();
                    System.out.println(Thread.currentThread().getName()+"正在运行。。。。");
                    if (finalI == 1) {
                        wthreads[1].interrupt();
                    }
                    condition.signal();//发送信号，唤醒其它线程
                    System.out.println(Thread.currentThread().getName()+"发送一个signal");
                    System.out.println(Thread.currentThread().getName()+"发送一个signal后，结束");
                    lock.unlock();
                }

            },"s" + i).start();
        }
    }

}