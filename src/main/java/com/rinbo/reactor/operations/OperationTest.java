package com.rinbo.reactor.operations;

import org.junit.Before;
import org.junit.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

public class OperationTest {

    @Before
    public void befor() {
        new CountDownLatch(1);
    }

    @Test
    public void testInterval() throws InterruptedException {
        CountDownLatch cdl = new CountDownLatch(1);
        Flux.interval(Duration.ofSeconds(1)).subscribe(System.out::println, System.err::println, () -> cdl.countDown());
        cdl.await();
    }

    @Test
    public void testGenerator1() {
        AtomicInteger cout = new AtomicInteger();
        Flux.generate(synchronousSink -> {
            synchronousSink.next("hello");
            if (cout.getAndIncrement() > 5) {
                synchronousSink.complete();
            }
        }).subscribe(System.out::println, System.err::println, System.out::println);
    }

    @Test
    public void testGenerator2() {
        final Random random = new Random();
        Flux.generate(ArrayList::new, (list, sink) -> {
            int value = random.nextInt(100);
            list.add(value);
            sink.next(value);
            if (list.size() == 10) {
                sink.complete();
            }
            return list;
        }).subscribe(System.out::println, System.err::println, System.out::println);
    }


    @Test
    public void testGenerator3() {
        final Random random = new Random();
        Flux.generate(ArrayList::new, (list, sink) -> {
            int value = random.nextInt(100);
            list.add(value);
            sink.next(value);
            if (list.size() == 5) {
                sink.complete();
            }
            return list;
        }, list -> {
            //调用sink.complete()后才会执行
            System.out.println("the list size is : " + list.size());
        }).subscribe(System.out::println, System.err::println, System.out::println);
    }

    @Test
    public void testBuffer() {
        Flux.just(1,2,3,4,5,6,7,8,9,10).buffer(5).subscribe(System.out::println);
    }

    @Test
    public void testBufferUntil() {
        Flux.range(1, 10).bufferUntil(i -> i % 2 == 0).subscribe(System.out::println);
    }

    @Test
    public void testBufferWhile() {
        Flux.range(1, 10).bufferWhile(i -> i % 2 == 0).subscribe(System.out::println);
    }

    @Test
    public void testWind() {
        Flux.just(1,2,3,4,5,6,7,8,9,10).window(4).subscribe(System.out::println);
    }

    @Test
    public void testContact() {
        Flux.just(1,2,3).concatWith(Mono.just(4)).subscribe(System.out::println);
    }

    @Test
    public void testMerge() {
        Flux.merge(Flux.just(1,2,3,4,5,6,7,8,9,10).take(5), Flux.just(11,12,13,14,15,16,17,18,19,20).take(5)).toStream().forEach(System.out::println);
        Flux.mergeSequential(Flux.just(1,2,3,4,5,6,7,8,9,10).take(5), Flux.just(11,12,13,14,15,16,17,18,19,20).take(5)).toStream().forEach(System.out::println);
    }

    @Test
    public void testCombineLatest() {
        Flux.combineLatest(
                Arrays::toString,
                Flux.just(1,2,3,4,5,6,7,8,9,10).take(5),
                Flux.just(11,12,13,14,15,16,17,18,19,20).take(5)
        ).toStream().forEach(System.out::println);
    }

    @Test
    public void testError1() {
        Flux.just(1, 2).concatWith(Mono.error(new IllegalArgumentException(""))).subscribe(System.out::println, System.err::println);
        //错误时返回0
        Flux.just(1, 2).concatWith(Mono.error(new IllegalArgumentException(""))).onErrorReturn(0).subscribe(System.out::println, System.err::println);
        //错误时根据异常类型来判断
        Flux.just(1, 2).concatWith(Mono.error(new IllegalArgumentException(""))).onErrorResume((e -> {
            if (e instanceof IllegalStateException) {
                return Mono.just(0);
            } else if (e instanceof IllegalArgumentException) {
                return Mono.just(-1);
            }
            return Mono.empty();
        })).subscribe(System.out::println, System.err::println);
        //错误时重试
        Flux.just(1, 2).concatWith(Mono.error(new IllegalArgumentException(""))).retry(2).subscribe(System.out::println, System.err::println);
    }
}
