package com.rinbo.reactor;

import org.junit.Test;
import reactor.core.Disposable;
import reactor.core.publisher.ConnectableFlux;
import reactor.core.publisher.Flux;
import reactor.core.publisher.UnicastProcessor;

import java.time.Duration;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class HotVsCold {

    @Test
    public void testHotSequence() {
        //热发布者
        UnicastProcessor<String> hostSource = UnicastProcessor.create();
        Flux<String> hotFlux = hostSource.publish().autoConnect().map(String::toUpperCase);

        hotFlux.subscribe(System.out::println);
        //使用onNext方法手动发出元素
        hostSource.onNext("aa");
        hostSource.onNext("bb");

        hotFlux.subscribe(d -> System.out.println("Subscriber 2 to Hot Source: " + d));

        hostSource.onNext("orange");
        hostSource.onNext("purple");
        hostSource.onComplete();
    }


    @Test
    public void testConnectableFlux() throws InterruptedException {
        Flux<Integer> source = Flux.range(1, 3).doOnSubscribe(s -> System.out.println("上游收到订阅"));

        ConnectableFlux<Integer> co = source.publish();
        co.subscribe(System.out::println, e -> {
        }, () -> {
        });
        co.subscribe(System.out::println, e -> {
        }, () -> {
        });
        System.out.println("订阅者完成订阅操作");
        Thread.sleep(500);
        System.out.println("还没有连接上");
        //当connect的时候，上游才真正收到订阅请求
        co.connect();
    }

    @Test
    public void testConnectableFluxAutoConnect() throws InterruptedException {
        Flux<Integer> source = Flux.range(1, 3)
                .doOnSubscribe(s -> System.out.println("上游收到订阅"));
        Flux<Integer> autoCo = source.publish().autoConnect(2);
        autoCo.subscribe(System.out::println, e -> {
        }, () -> {
        });
        System.out.println("第一个订阅者完成订阅操作");
        Thread.sleep(500);
        System.out.println("第二个订阅者完成订阅操作");
        //只有两个订阅者都完成订阅之后，上游才收到订阅请求，并开始发出数据
        autoCo.subscribe(System.out::println, e -> {
        }, () -> {
        });
    }

    @Test
    public void testConnectableFluxRefConnect() throws InterruptedException {
        Flux<Long> source = Flux.interval(Duration.ofMillis(500))
                .doOnSubscribe(s -> System.out.println("上游收到订阅"))
                .doOnCancel(() -> System.out.println("上游发布者断开连接"));
        //当所有订阅者都取消时，如果不能在两秒内接入新的订阅者，则上游会断开连接
        Flux<Long> refCounted = source.publish().refCount(2, Duration.ofSeconds(2));
        System.out.println("第一个订阅者订阅");
        Disposable sub1 = refCounted.subscribe(l -> System.out.println("sub1: " + l));

        TimeUnit.SECONDS.sleep(1);
        System.out.println("第二个订阅者订阅");
        Disposable sub2 = refCounted.subscribe(l -> System.out.println("sub2: " + l));

        TimeUnit.SECONDS.sleep(1);
        System.out.println("第一个订阅者取消订阅");
        sub1.dispose();

        TimeUnit.SECONDS.sleep(1);
        System.out.println("第二个订阅者取消订阅");
        sub2.dispose();

        TimeUnit.SECONDS.sleep(1);
        System.out.println("第三个订阅者订阅");
        Disposable sub3 = refCounted.subscribe(l -> System.out.println("sub3: " + l));

        TimeUnit.SECONDS.sleep(1);
        System.out.println("第三个订阅者取消订阅");
        sub3.dispose();

        //第四个订阅者没能在2秒内开始订阅，所以上游发布者断开连接
        TimeUnit.SECONDS.sleep(3);
        System.out.println("第四个订阅者订阅");
        Disposable sub4 = refCounted.subscribe(l -> System.out.println("sub4: " + l));
        TimeUnit.SECONDS.sleep(1);
        System.out.println("第五个订阅者订阅");
        Disposable sub5 = refCounted.subscribe(l -> System.out.println("sub5: " + l));
        TimeUnit.SECONDS.sleep(2);
    }

    @Test
    public void testConnectableFluxRepay() throws InterruptedException {
        CountDownLatch cd = new CountDownLatch(1);
        Flux<Long> source = Flux.interval(Duration.ofSeconds(1))
                .doOnSubscribe(s -> System.out.println("上游收到订阅"));
        //replay缓存最新数据
        Flux<Long> replay = source.replay(4).autoConnect();
        replay.subscribe(l -> System.out.println("sub1: " + l), System.err::println, () -> cd.countDown());
        TimeUnit.SECONDS.sleep(10);
        replay.subscribe(l -> System.out.println("sub2: " + l), System.err::println, () -> cd.countDown());
        cd.await();
    }
}
