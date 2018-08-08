package com.rinbo.reactor.publisher;

import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

public abstract class RBFlux<T> implements Publisher<T> {

    @Override
    public abstract void subscribe(Subscriber<? super T> s);

    public void subscribe(Consumer<? super T> consumer, Consumer<? super Throwable> errorConsumer, Runnable completeConsumer, Consumer<? super Subscription> subscriptionConsumer) {
        subscribe(new LambdaSubscriber<>(consumer, errorConsumer, completeConsumer, subscriptionConsumer));
    }

    public void subscribe(Consumer<? super T> consumer) {
        subscribe(consumer, null, null, null);
    }

    public void subscribe(Consumer<? super T> consumer, Consumer<? super Throwable> errorConsumer) {
        subscribe(consumer, errorConsumer, null, null);
    }

    public void subscribe(Consumer<? super T> consumer, Consumer<? super Throwable> errorConsumer, Runnable completeConsumer) {
        subscribe(consumer, errorConsumer, completeConsumer, null);
    }

    public static <T> RBFlux<T> just(T... data) {
        return new RBFluxArray<>(data);
    }

    //泛型方法，通过泛型表示可能出现的类型的变化
    public <V> RBFlux<V> map(Function<? super T, ? extends V> mapper) {
        //FluxMap就是新的Flux
        return new RBFluxMap<>(this, mapper);
    }

    public RBFlux<T> filter(Predicate<T> p) {
        return new RBFluxFilter<T>(this,p);
    }
}