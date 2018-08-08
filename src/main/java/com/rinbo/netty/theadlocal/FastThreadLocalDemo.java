package com.rinbo.netty.theadlocal;

import io.netty.util.concurrent.FastThreadLocal;
import io.netty.util.concurrent.FastThreadLocalThread;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;

@Slf4j
public class FastThreadLocalDemo {

    @Data
    @AllArgsConstructor
    class Persion {
        private int id;
        private String name;
    }

    class MyFastThreadLocal extends FastThreadLocal<Persion> {
        @Override
        protected Persion initialValue() throws Exception {
            return new Persion(1, "33");
        }

        @Override
        protected void onRemoval(Persion value) throws Exception {
            System.out.println(value + " is removed!");
        }
    }


    @Test
    public void test1() throws InterruptedException {
        CountDownLatch ctd = new CountDownLatch(2);
        new Thread(() -> {
            MyFastThreadLocal ftl = new MyFastThreadLocal();
            Persion persion = ftl.get();
            log.debug("result : {}", persion);
            ctd.countDown();
            MyFastThreadLocal ftl2 = new MyFastThreadLocal();
            Persion persion2 = ftl2.get();
            log.debug("result : {}", persion2);
            ctd.countDown();
        }).start();
        System.gc();
        ctd.await();
    }

}
