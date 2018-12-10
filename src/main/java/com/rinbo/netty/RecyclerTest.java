package com.rinbo.netty;

import io.netty.util.Recycler;
import org.junit.Test;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public class RecyclerTest {

    private static CountDownLatch latch = new CountDownLatch(4);
    private static List<MyObj> objs1 = new LinkedList<>();

    @Test
    public void testRecyclerGet() throws InterruptedException {
        System.setProperty("io.netty.recycler.ratio","4");
        new Thread(() -> {
            for (int i = 0; i < 20; i++) {
                objs1.add(MyObj.newInstance());
            }
//            for (int i = 0; i < 2; i++) {
//                MyObj obj = objs1.get(i);
//                if (obj !=null) {
//                    obj.recycle();
//                }
//            }
            for (int i = 0; i < 5; i++) {
                objs1.add(MyObj.newInstance());
            }
            latch.countDown();
        }, "Add thread").start();
        new Thread(() -> {
            for (int i = 2; i < 5; i++) {
                MyObj obj = objs1.get(i);
                if (obj !=null) {
                    obj.recycle();
                }
            }
            latch.countDown();
        }, "Recycle-1 thread").start();

        new Thread(() -> {
            for (int i = 5; i < 20; i++) {
                MyObj obj = objs1.get(i);
                if (obj !=null) {
                    obj.recycle();
                }
            }
            latch.countDown();
        }, "Recycle-2 thread").start();
        latch.await();
    }
}

class MyObj {
    //对象池，用于缓存对象
    private static Recycler<MyObj> pool = new Recycler<MyObj>() {
        @Override
        protected MyObj newObject(Handle<MyObj> handle) {
            return new MyObj(handle);
        }
    };

    //属性
    Recycler.Handle<MyObj> handle;

    //构造函数，私有，不能被在外部创建
    private MyObj(Recycler.Handle<MyObj> handle) {
        this.handle = handle;
    }

    //唯一创建对象的方式
    public static MyObj newInstance() {
        return pool.get();
    }

    //回收当前对象
    public void recycle() {
        handle.recycle(this);
    }
}