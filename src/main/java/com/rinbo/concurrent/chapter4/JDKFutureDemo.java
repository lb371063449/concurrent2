package com.rinbo.concurrent.chapter4;

import java.util.concurrent.*;

public class JDKFutureDemo {
    static class RealData implements Callable<String> {
        private String data;
        public RealData(String data) {
            this.data = data;
        }
        public String call() throws Exception {
            StringBuffer sb = new StringBuffer();
            for (int i=0;i<10;i++) {
                sb.append(data + " : ");
                Thread.sleep(1000);
            }
            return sb.toString();
        }
    }

    public static void main(String[] args) throws InterruptedException, ExecutionException {
        RealData realData = new RealData("hello");
        FutureTask<String> task = new FutureTask<>(realData);
        ExecutorService service = Executors.newFixedThreadPool(1);
        service.submit(task);
        Thread.sleep(1000);
        System.out.println("request completed !");
        //如果call方法没有准备好依然会等待
        System.out.println(task.get());
    }
}
