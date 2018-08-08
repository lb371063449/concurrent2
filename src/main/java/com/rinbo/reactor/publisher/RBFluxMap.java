package com.rinbo.reactor.publisher;

import lombok.AllArgsConstructor;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.util.function.Function;

//中间源
@AllArgsConstructor
public class RBFluxMap<T, R> extends RBFlux<R> {
    //上游源
    private final RBFlux<? extends T> source;
    //opration
    private final Function<? super T, ? extends R> mapper;

    @Override
    public void subscribe(Subscriber<? super R> actual) {
        //将当前流的订阅者注册到上游流，相当于将订阅者向上传递
        source.subscribe(new MapSubscriber<>(actual, mapper));
    }

    //订阅者，订阅者是从下往上传递
    static final class MapSubscriber<T, R> implements Subscriber<T>, Subscription {
        //下游订阅者
        private final Subscriber<? super R> actual;
        //operation
        private final Function<? super T, ? extends R> mapper;

        boolean done;

        Subscription subscriptionOfUpstream;

        MapSubscriber(Subscriber<? super R> actual, Function<? super T, ? extends R> mapper) {
            this.actual = actual;
            this.mapper = mapper;
        }

        @Override
        public void onSubscribe(Subscription s) {
            //拿到来自上游的Subscription
            this.subscriptionOfUpstream = s;
            //回调下游的onSubscribe，将自身作为Subscription传递过去；
            actual.onSubscribe(this);
        }

        @Override
        public void onNext(T t) {
            if (done) {
                return;
            }
            //收到上游发出的数据后，将其用mapper进行转换，然后接着发给下游；
            actual.onNext(mapper.apply(t));
        }

        @Override
        public void onError(Throwable t) {
            if (done) {
                return;
            }
            done = true;
            //将上游的错误信号原样发给下游；
            actual.onError(t);
        }

        @Override
        public void onComplete() {
            if (done) {
                return;
            }
            done = true;
            //将上游的完成信号原样发给下游；
            actual.onComplete();
        }

        //map操作符并不产生数据，只是数据的搬运工。收到request后要发出的数据来自上游
        @Override
        public void request(long n) {
            //将下游的请求传递给上游
            this.subscriptionOfUpstream.request(n);
        }

        @Override
        public void cancel() {
            //将下游的取消操作传递给上游
            this.subscriptionOfUpstream.cancel();
        }
    }
}