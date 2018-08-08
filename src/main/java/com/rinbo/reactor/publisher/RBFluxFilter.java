package com.rinbo.reactor.publisher;

import lombok.AllArgsConstructor;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;

//中间源
@AllArgsConstructor
public class RBFluxFilter<T> extends RBFlux<T> {
    //上游源
    private RBFlux<T> source;
    //opration
    private Predicate<T> p;

    @Override
    public void subscribe(Subscriber<? super T> actor) {
        source.subscribe(new FilterSubscriber(actor,p));
    }

    static final class FilterSubscriber<T> implements Subscriber<T>,Subscription {

        private Subscriber actor;
        Subscription subscription;
        private Predicate<T> p;

        public FilterSubscriber(Subscriber actor, Predicate<T> p) {
            this.actor = actor;
            this.p = p;
        }

        @Override
        public void onSubscribe(Subscription subscription) {
            this.subscription = subscription;
            actor.onSubscribe(this);
        }

        @Override
        public void onNext(Object o) {
            new Thread(()->{
                try {
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (p.test((T) o)) {
                    actor.onNext(o);
                }
            }).start();
        }

        @Override
        public void onError(Throwable t) {

        }

        @Override
        public void onComplete() {

        }

        @Override
        public void request(long n) {
            this.subscription.request(n);
        }

        @Override
        public void cancel() {

        }
    }
}
