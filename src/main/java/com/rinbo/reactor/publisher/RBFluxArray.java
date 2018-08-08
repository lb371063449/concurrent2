package com.rinbo.reactor.publisher;

import lombok.AllArgsConstructor;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

//源数据流
@AllArgsConstructor
public class RBFluxArray<T> extends RBFlux<T> {
    //源数据
    private T[] array;

    @Override
    public void subscribe(Subscriber<? super T> actual) {
        //源数据流中执行订阅者onSubscribe
        actual.onSubscribe(new ArraySubscription<>(actual, array));
    }

    //订阅信息，持有数据源和订阅者，用于数据源和订阅者之间通信
    static class ArraySubscription<T> implements Subscription {
        //订阅者
        final Subscriber<? super T> actual;
        //数据源数据
        final T[] array;
        int index;
        boolean canceled;

        public ArraySubscription(Subscriber<? super T> actual, T[] array) {
            this.actual = actual;
            this.array = array;
        }

        @Override
        public void request(long n) {
            if (canceled) {
                return;
            }
            long length = array.length;
            for (int i = 0; i < n && index < length; i++) {
                if (canceled) {
                    return;
                }
                //当有可以发出的元素时，回调订阅者的onNext方法传递元素
                actual.onNext(array[index++]);
            }
            //当所有的元素都发完时，回调订阅者的onComplete方法
            if (index == length) {
                actual.onComplete();
            }
        }

        //订阅者可以使用Subscription取消订阅
        @Override
        public void cancel() {
            this.canceled = true;
        }
    }
}
