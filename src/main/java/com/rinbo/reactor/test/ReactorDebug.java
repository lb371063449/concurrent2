package com.rinbo.reactor.test;

import org.junit.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Hooks;
import reactor.core.publisher.Mono;

import java.time.Duration;

public class ReactorDebug {
    private Mono<Integer> getMonoWithException() {
        return Flux.<Integer>range(1, 5)
                .map(i -> i * i)
                .filter(i -> (i % 2) == 0)
                //single()方法只能接收一个元素，多了的话就会导致异常
                .single();
    }

    //1、Hooks.onOperatorDebug()
    @Test
    public void testBug() {
        //Hooks.onOperatorDebug()的实现原理在于在组装期包装各个操作符的构造方法，加入一些监测功能
        Hooks.onOperatorDebug();
        getMonoWithException().subscribe();
    }

    //2、使用 checkpoint() 来定位链
    @Test
    public void checkBugWithCheckPoint() {
        getMonoWithException()
                //查找问题出在哪个链上
                .checkpoint()
                .subscribe();
    }

    @Test
    public void checkBugWithCheckPoint2() {
        getMonoWithException()
                //传入一个独特的字符串以方便在 assembly traceback 中进行识别
                .checkpoint("checkBugWithCheckPoint2", true)
                .subscribe();
    }

    //它能够记录其上游的Flux或 Mono的事件（包括onNext、onError、onComplete， 以及onSubscribe、cancel、和request）
    @Test
    public void testLog1() {
        Integer integer = Flux.range(1, 10)
                .take(3)
                .log()
                //订阅此Flux并无限期阻塞，直到上游发出最后一个值或完成。 返回该值，如果Flux完成为空，则返回null。 如果Flux错误，则抛出原始异常
                .blockLast();
        System.out.println(integer);
    }

    @Test
    public void testLog2() {
        Flux.range(1, 10)
                .delayElements(Duration.ofMillis(10))
                .log()
                .take(3)
                .blockLast();
    }
}
