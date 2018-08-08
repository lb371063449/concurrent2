package com.rinbo.reference;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.junit.Test;

import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class ReferenceTest {

    @Data
    @AllArgsConstructor
    static class ProductA {
        String no;
        String name;
    }

    @Test
    public void test1() {
        ProductA a = new ProductA("aa","bb");
        WeakReference<ProductA> weakProductA = new WeakReference<>(a);
        //通过weakProduct.get()获取它所指向的Product对象
        ProductA product = weakProductA.get();
    }

    /**
     * Created by robin.yzb
     * 为了确保System.gc()后,SoftReference引用的referent被回收需要加入下面的参数
     * -XX:SoftRefLRUPolicyMSPerMB=0
     */
    @Test
    public void test2() throws InterruptedException {
        List<Reference> roots = new ArrayList<>();
        ReferenceQueue rq = new ReferenceQueue();
        new Thread(() ->{
                int i=0;
                while (true) {
                    try {
                        Reference r = rq.remove();
                        System.out.println("reference:"+r);
                        //为null说明referent被回收
                        System.out.println( "get:"+r.get());
                        i++;
                        System.out.println( "queue remove num:"+i);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

        }).start();
        for(int i=0;i<100000;i++) {
            byte[] a = new byte[1024*1024];
            // 分别验证SoftReference,WeakReference,PhantomReference
            Reference r = new SoftReference(a, rq);
            //Reference r = new WeakReference(a, rq);
            //Reference r = new PhantomReference(a, rq);
            roots.add(r);
            System.gc();
            System.out.println("produce"+i);
            TimeUnit.MILLISECONDS.sleep(100);
        }
    }
}
