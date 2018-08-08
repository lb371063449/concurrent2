package com.rinbo.reactor.operations;

import org.junit.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.Duration;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

public class OperationTest2 {

    @Test
    public void test1() {
        Flux.just("a", "b")
                .doOnNext(System.out::println)
                .subscribe(System.out::println);
    }

    @Test
    public void test2() throws InterruptedException {
        Flux.just("a", "b", "c", "d")
                .delayElements(Duration.ofSeconds(1))
                .subscribe(i -> System.out.println(i + " : " + Thread.currentThread().getName()));

        TimeUnit.SECONDS.sleep(10);
    }

    @Test
    public void test3() throws InterruptedException {
        Mono blockingWrapper = Mono.fromCallable(() -> {
            TimeUnit.SECONDS.sleep(2);
            return "success";
        });
        blockingWrapper = blockingWrapper.subscribeOn(Schedulers.elastic());
        blockingWrapper.subscribe(System.out::println);

        TimeUnit.SECONDS.sleep(3);
        System.out.println("complete");
    }

    @Test
    public void test4() {
        Flux<String> flux = Flux.just("foo", "chain");
        flux.map(secret -> secret.replaceAll(".", "*"));
        flux.subscribe(next -> System.out.println("Received: " + next));
    }

    @Test
    public void testTransform() {
        Function<Flux<String>, Flux<String>> filterAndMap = f -> f.filter(color -> !color.equals("orange")).map(String::toUpperCase);
        Flux.fromIterable(Arrays.asList("blue", "green", "orange", "purple"))
                .doOnNext(System.out::println)
                //将transform中的oprations作用到transform()前的original sequence
                .transform(filterAndMap)
                .subscribe(d -> System.out.println("Subscriber to Transformed MapAndFilter: " + d));
    }

    @Test
    public void testCompose() {
        AtomicInteger ai = new AtomicInteger();
        Function<Flux<String>, Flux<String>> filterAndMap = f -> {
            if (ai.incrementAndGet() == 1) {
                return f.filter(color -> !color.equals("orange"))
                        .map(String::toUpperCase);
            }
            return f.filter(color -> !color.equals("purple"))
                    .map(String::toUpperCase);
        };
        Flux<String> composedFlux =
                Flux.fromIterable(Arrays.asList("blue", "green", "orange", "purple"))
                        .doOnNext(System.out::println)
                        //为每个subscriber 生成一个新的original sequence
                        //original sequence的内容为compose()前面部分
                        .compose(filterAndMap);

        composedFlux.subscribe(d -> System.out.println("Subscriber 1 to Composed MapAndFilter :" + d));
        composedFlux.subscribe(d -> System.out.println("Subscriber 2 to Composed MapAndFilter: " + d));
    }

    @Test
    public void testGroupBy() {
        Flux.just(1, 3, 5, 2, 4, 6, 11, 12, 13)
                .groupBy(i -> i % 2 == 0 ? "even" : "odd")
                .concatMap(g -> g.defaultIfEmpty(-1)
                        .map(String::valueOf)
                        .startWith(g.key()))
                .subscribe(System.out::println);

    }

    @Test
    public void testContext() {
        String key = "message";
        Mono<String> r = Mono.just("Hello")
                .flatMap( s -> Mono.subscriberContext()
                        .map( ctx -> s + " " + ctx.get(key)))
                .subscriberContext(ctx -> ctx.put(key, "World"));
        r.subscribe(System.out::println);
    }
}
