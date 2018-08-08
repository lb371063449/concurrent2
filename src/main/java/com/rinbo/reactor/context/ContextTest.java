package com.rinbo.reactor.context;

import org.junit.Test;
import reactor.core.publisher.Mono;

public class ContextTest {

    @Test
    public void test1() throws InterruptedException {
        String key = "message";
        Mono<String> r = Mono.just("Hello")
                .flatMap(s -> Mono.subscriberContext()
                        .map( ctx -> s + " " + ctx.get(key)))
                .subscriberContext(ctx -> ctx.put(key, "World"));
        r.subscribe(System.out::println);
    }
}


