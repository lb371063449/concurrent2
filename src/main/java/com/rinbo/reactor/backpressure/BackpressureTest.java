package com.rinbo.reactor.backpressure;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import reactor.core.publisher.BaseSubscriber;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;
import reactor.core.scheduler.Schedulers;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class BackpressureTest {
    //生成的事件间隔时间，单位毫秒
    private final int EVENT_DURATION = 10;
    //生成的事件个数
    private final int EVENT_COUNT = 20;
    //订阅者处理每个元素的时间，单位毫秒
    private final int PROCESS_DURATION = 30;

    CountDownLatch countDownLatch;
    EventSource eventSource = new EventSource();
    Subscriber slowSubscriber;
    private Flux<Event> fastPublisher;

    //慢订阅者类
    class SlowSubscriber extends BaseSubscriber<Event> {

        @Override
        protected void hookOnSubscribe(Subscription subscription) {
            // 订阅时请求1个数据
            request(1);
        }

        @Override
        protected void hookOnNext(Event event) {
            System.out.println("         consumer <<< " + event.getName() + "  " + event.getId());
            try {
                TimeUnit.MILLISECONDS.sleep(PROCESS_DURATION);
            } catch (InterruptedException e) {
            }
            //每处理完1个数据，就再请求1个
            request(1);
        }

        @Override
        protected void hookOnError(Throwable throwable) {
            System.err.println("         consumer <<< " + throwable);
        }

        @Override
        protected void hookOnComplete() {
            countDownLatch.countDown();
        }
    }

    @Before
    public void setup() {
        countDownLatch = new CountDownLatch(1);
        slowSubscriber = new SlowSubscriber();
        eventSource = new EventSource();
    }

    @After
    public void subscribe() throws InterruptedException {
        fastPublisher.subscribe(slowSubscriber);
        generateEvent(EVENT_COUNT, EVENT_DURATION);
        countDownLatch.await(1, TimeUnit.MINUTES);
    }

    public Flux createFlux(FluxSink.OverflowStrategy strategy) {
        return Flux.create(fluxSink -> {
            eventSource.addListener(new EventListener() {
                @Override
                public void onEvent(Event event) {
                    System.out.println("publish >>> " + event.getName());
                    fluxSink.next(event);
                }

                @Override
                public void onStop() {
                    fluxSink.complete();
                }
            });
        }, strategy);
    }

    // 循环生成MyEvent，每个MyEvent间隔millis毫秒
    private void generateEvent(int times, int millis) {
        for (int i = 0; i < times; i++) {
            try {
                TimeUnit.MILLISECONDS.sleep(millis);
            } catch (InterruptedException e) {
            }
            eventSource.onEvent(new Event(Thread.currentThread().getName(), i));
        }
        eventSource.onStop();
    }

    @Test
    public void testCreateBackPressureStratety() {
        fastPublisher = createFlux(FluxSink.OverflowStrategy.DROP)
                .doOnRequest(n -> System.out.println("         ===  request: " + n + " ==="))
                .publishOn(Schedulers.newSingle("newSingle"), 1);
    }


}
