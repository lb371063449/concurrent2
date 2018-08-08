package com.rinbo.concurrent.chapter3;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Semaphore;

public class SemapDemo2 {
    private static class BoundedHashSet<T> {
        private final Set<T> set;
        private final Semaphore sem;

        public BoundedHashSet(int bound) {
            this.set = Collections.synchronizedSet(new HashSet<>());
            //bound最多允许同时执行的线程数量
            this.sem = new Semaphore(bound);
        }

        //最多只能有bound个add线程同时写入，等待删除是否锁
        public boolean add(T o) throws InterruptedException {
            //获得锁
            sem.acquire();
            boolean wasAdded = false;
            try {
                wasAdded = set.add(o);
                System.out.println(o + " : was added to set");
                return wasAdded;
            } finally {
                //如果添加失败，才释放锁
                if (!wasAdded) {
                    sem.release();
                }
            }
        }

        public boolean remove(T o) throws InterruptedException {
            boolean remove = set.remove(o);
            if (remove) {
                System.out.println(o + " : was removed to set");
                //移除才释放锁，是释放所有的锁，此时可以同时允许bound个线程新增
                Thread.sleep(1000);
                sem.release();
            }
            return remove;
        }
    }

    public static void main(String[] args) {
        BoundedHashSet semBuond = new BoundedHashSet<Integer>(5);
        //写线程
        for (int i=0;i<100;i++) {
            int finalI = i;
            new Thread() {
                @Override
                public void run() {
                    try {
                        semBuond.add(Integer.valueOf(finalI));
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }.start();
        }
        //删除操作线程
        for (int i=0;i<10;i++) {
            int finalI = i;
            new Thread() {
                @Override
                public void run() {
                    try {
                        semBuond.remove(Integer.valueOf(finalI));
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }.start();
        }
    }
}
