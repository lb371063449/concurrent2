package com.rinbo.concurrent.chapter7;

import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.*;

public class NewTaskForDemo {

    //扩展Callable
    interface CancellableTask<T> extends Callable<T> {
        void cancel();
        RunnableFuture<T> newTask();
    }

    //扩展ThreadPoolExecutor的newTaskFor方法
    class CancellingExecutor extends ThreadPoolExecutor {

        public CancellingExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue) {
            super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue);
        }

        //newTaskFor方法返回RunnableFuture
        @Override
        protected<T> RunnableFuture<T> newTaskFor(Callable<T> callable){
            if (callable instanceof CancellableTask) {
                return ((CancellableTask) callable).newTask();
            } else {
                return super.newTaskFor(callable);
            }
        }
    }

    //具体的Task
    abstract class SocketUsingTask<T> implements CancellableTask<T> {
        private Socket socket;

        protected synchronized void setSocket(Socket socket) {
            this.socket = socket;
        }

        //取消
        @Override
        public synchronized void cancel() {
            try {
                if (socket != null) {
                    socket.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        //由于FutureTask是RunnableFuture的子类，应此可以直接返回FutureTask实例
        @Override
        public RunnableFuture<T> newTask() {
            return new FutureTask<T>(this) {

                @Override
                public boolean cancel(boolean mayInterruptIfRunning) {
                    try {
                        //调用CancellableTask的cancel方法
                        SocketUsingTask.this.cancel();
                    } finally {
                        return super.cancel(mayInterruptIfRunning);
                    }
                }
            };
        }
    }
}
