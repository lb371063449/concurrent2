package com.rinbo.reactor.publisher;

import org.junit.Test;

import java.util.concurrent.TimeUnit;


/**
 * 步骤一：建立连接
 * 1、数据流
 * 源数据流，保存源数据
 * 中间流，保存上游的数据流，用于传递订阅者
 * 2、订阅操作
 * a、最后一个数据源调用subscribe()，将终端订阅者传入
 * b、订阅者从后向上传递(通过中调用间流中的上游数据流subscribe())，传入新的订阅者(当前接受到的订阅者+处理函数)到上游数据源
 * 3、订阅者到达最终数据源
 * 步骤二：数据源发送订阅信息，调用订阅者的onSubscribe()
 * 1、源数据流将自己的数据和订阅者信息封装成订阅信息(Subscription)，发送给当前订阅者
 * 2、中间订阅者保存接收到的订阅信息，调用2.b中接受到的下游订阅者的onSubscribe()，传递新的订阅信息
 * 3、执行最终订阅者onSubscribe()，调用订阅信息的request()方法请求数据
 * 步骤三：订阅者请求数据，从订阅信息中request()
 * 1、中间订阅信息的request()中直接调用(步骤二中的第2步中保留的上游订阅信息)的request()
 * 2、源数据流发出的订阅信息收到request()请求，发射数据给客服端
 * 步骤四：推送数据给订阅者
 * 1、源数据流发出的订阅信息中包含一下个订阅者，因此调用下一个订阅者的onNext()方法
 * 2、中间数据流发出的订阅者，在onNext()中对数据处理收发送给下一个订阅者
 * 3、最终订阅者接收经过处理过的推送数据
 *
 * 总结：
 * 中间数据流，持有上游数据流对象，用于传递下游订阅者给上游数据流
 * 中间订阅者，持有下游订阅者，用于传递订阅信息到下游
 * 中间订阅信息，保留上游订阅信息，用于传递request()，保存操作函数，用于对数据进行处理
 */
public class RBTest {

    @Test
    public void fluxMapTest() throws InterruptedException {
        RBFlux.just(1, 2, 3, 4, 5, 6)
                .map(i -> i * i)
                .filter(i -> i % 2 == 0)
                .subscribe(new RBSubscriber());

        System.out.println("aa");
        TimeUnit.SECONDS.sleep(10);
    }

    @Test
    public void lambdaSubscriberTest() {
        RBFlux.just(1, 2, 3, 4, 5)
                .map(i -> i * 2)
                .subscribe(
                        System.out::println,
                        System.err::println,
                        () -> System.out.println("Completed.")
//                        subscription -> subscription.request(3)
                );
    }
}
