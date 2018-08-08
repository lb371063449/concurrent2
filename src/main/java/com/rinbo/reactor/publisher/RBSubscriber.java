package com.rinbo.reactor.publisher;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

//订阅者
public class RBSubscriber implements Subscriber {
    //数据源终点会调用Subscriber的onSubscribe
    //传入的参数Subscription持有数据源和订阅者对象
    @Override
    public void onSubscribe(Subscription s) {
        System.out.println("onSubscribe");
        s.request(6);
    }

    @Override
    public void onNext(Object o) {
        //Subscription#request()中会调用订阅者的onNext
        System.out.println(o);
    }

    @Override
    public void onError(Throwable t) {
        //Subscription#request()出错时调用
        System.err.println(t.getMessage());
    }

    @Override
    public void onComplete() {
        //Subscription#request()中，数据源中的数据发送完时调用
        System.out.println("onComplete");
    }
}
