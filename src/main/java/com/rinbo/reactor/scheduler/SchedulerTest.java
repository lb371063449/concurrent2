package com.rinbo.reactor.scheduler;

import org.junit.Test;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

import java.time.Duration;

public class SchedulerTest {

    @Test
    public void testScheduling() throws InterruptedException {
        Flux.just(1,2,3,4).publishOn(Schedulers.newParallel("myParallel")).map(i->i*2).subscribe(System.out::println);
//        Flux.just(1,2,3,4).subscribeOn(Schedulers.newParallel("myParallel")).map(i->i*2).subscribe(System.out::println);

    }

    @Test
    public void testDelayElements() {
        Flux.range(0, 10)
                .delayElements(Duration.ofMillis(10))
                .log()
                .blockLast();
    }

    @Test
    public void test3() throws InterruptedException {
        Flux.create(sink -> {
            sink.next(Thread.currentThread().getName());
            sink.complete();
        })
                .publishOn(Schedulers.single())
                .map(x -> String.format("[%s] %s", Thread.currentThread().getName(), x))
                .publishOn(Schedulers.elastic())
                .map(x -> String.format("[%s] %s", Thread.currentThread().getName(), x))
                .subscribeOn(Schedulers.parallel())
                .toStream()
                .forEach(System.out::println);
    }

    @Test
    public void test4() {
        Flux.just(Thread.currentThread().getName()).publishOn(Schedulers.single()).map(i -> Thread.currentThread().getName() + "---" + i)
                .subscribe(System.out::println);

        Flux.just(Thread.currentThread().getName()).publishOn(Schedulers.single()).map(i -> Thread.currentThread().getName() + "---" + i)
                .subscribe(System.out::println);
    }

    @Test
    public void test5() {
        Flux.range(1, 10).log().map(p -> Thread.currentThread().getName() + "--" + p)
                .subscribe(System.out::println);
    }
}
