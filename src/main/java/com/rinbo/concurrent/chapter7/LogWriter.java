package com.rinbo.concurrent.chapter7;

import java.io.PrintWriter;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

public class LogWriter {
    private final BlockingQueue<String> queue;
    private final LoggerThread loggerThread;
    private final PrintWriter writer;
    private boolean isShutdown;
    //记录添加到队列中的日志数量
    private int reservations;

    public LogWriter(PrintWriter writer) {
        this.queue = new LinkedBlockingDeque<>(16);
        this.loggerThread = new LoggerThread(writer);
        this.writer = writer;
    }

    //启动消费日志的线程
    public void start() {
        loggerThread.start();
    }

    //关闭生产者-消费者线程
    public void stop() {
        //由于log的实现是一种先判断在运行的代码序列，因此isShutdown必须在同步代码块中
        synchronized (this) {
            isShutdown = true;
        }
        //loggerThread可能阻塞在获取日志的消息队列上，所以要中断
        loggerThread.interrupt();
    }

    //写入日志，相当于将日志写入到消息队列中
    public void log(String msg) throws InterruptedException {
        synchronized (this) {
            if (isShutdown) {
                throw new IllegalStateException("");
            }
            ++reservations;
        }
        queue.put(msg);
    }

    private class LoggerThread extends Thread {
        private final PrintWriter printWriter;

        private LoggerThread(PrintWriter printWriter) {
            this.printWriter = printWriter;
        }

        @Override
        public void run() {
            try {
                while (true) {
                    synchronized (LogWriter.this) {
                        //只有添加到队列中的日志全部消费，才退出线程
                        if (isShutdown && reservations == 0) {
                            break;
                        }
                    }
                    //可能会阻塞在队列上
                    String msg = queue.take();
                    synchronized (LogWriter.this) {
                        --reservations;
                    }
                    printWriter.println(msg);
                }
            } catch (InterruptedException e) {
                //响应中断，不做任何处理，重新尝试
            } finally {
                printWriter.close();
            }
        }
    }
}
