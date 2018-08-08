package com.rinbo.reactor.test;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Duration;

public class ReactorTest {

    private Flux<String> appendBoomError(Flux<String> origin) {
        return origin.concatWith(Mono.error(new RuntimeException("boom")));
    }

    @org.junit.Test
    public void testWithStepVerifier() {
        Flux<String> source = Flux.just("foo", "bar");
        //使用create方法创建基于Flux或Mono的StepVerifier
        StepVerifier.create(appendBoomError(source))
                //测试期望发出的下一个信号
                .expectNext("foo")
                //跳过部分序列或者对信号内容进行自定义校验
                .consumeNextWith(System.out::println)
                //终止事件，相应的期望方法
                .expectErrorMessage("boom")
                //终止后只能调用StepVerifier#verify()触发校验
                .verify();
    }

    @org.junit.Test
    public void testVirtualTime() {
        StepVerifier.withVirtualTime(() -> Mono.delay(Duration.ofDays(1)))
                .expectSubscription()
                .expectNoEvent(Duration.ofDays(1))  // 2
                .expectNext(0L)
                .verifyComplete();
    }

}
