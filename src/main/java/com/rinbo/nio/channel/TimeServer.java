package com.rinbo.nio.channel;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Calendar;
import java.util.concurrent.*;

public class TimeServer {

    private BlockingQueue<SocketChannel> idleQueue = new LinkedBlockingDeque<>();
    private BlockingQueue<Future<SocketChannel>> workingQueue = new LinkedBlockingDeque<>();
    private ExecutorService executorService = Executors.newSingleThreadExecutor();

    //启动轮询线程
    {
        new Thread() {
            @Override
            public void run() {
                while (true) {
                    try {
                        SocketChannel socketChannel = idleQueue.take();
                        //如果没有关闭，则继续添加到workingQueue队列
                        if (!socketChannel.socket().isClosed()) {
                            TimeServerHandleTask task = new TimeServerHandleTask(socketChannel);
                            Future<SocketChannel> future = executorService.submit(task, socketChannel);
                            workingQueue.add(future);
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();

        new Thread() {
            @Override
            public void run() {
                while (true) {
                    try {
                        Future<SocketChannel> future = workingQueue.take();
                        if (!future.isDone()) {
                            workingQueue.add(future);
                            continue;
                        }
                        SocketChannel socketChannel = null;
                        try {
                            socketChannel = future.get();
                            idleQueue.add(socketChannel);
                        } catch (Exception e) {
                            try {
                                socketChannel.close();
                                e.printStackTrace();
                            } catch (IOException e1) {
                                e1.printStackTrace();
                            }
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();
    }

    private static class TimeServerHandleTask implements Runnable {

        private SocketChannel socketChannel;

        public TimeServerHandleTask(SocketChannel socketChannel) {
            this.socketChannel = socketChannel;
        }

        @Override
        public void run() {
            try {
                ByteBuffer requestBuffer = ByteBuffer.allocate("GET CURRENT TIME".length());
                //尝试读取数据，因为是非阻塞，所以如果没有数据会立即返回。
                int bytesRead = socketChannel.read(requestBuffer);
                //如果没有读取到数据，说明当前SocketChannel并没有发送请求，不需要处理
                if (bytesRead <= 0) {
                    return;
                }
                //如果读取到了数据，则需要考虑粘包、解包问题，这个while代码是为了读取一个完整的请求信息"GET CURRENT TIME"，
                while (requestBuffer.hasRemaining()) {
                    socketChannel.read(requestBuffer);
                }
                String requestStr = new String(requestBuffer.array());
                if (!"GET CURRENT TIME".equals(requestStr)) {
                    String bad_request = "BAD_REQUEST";
                    ByteBuffer responseBuffer = ByteBuffer.allocate(bad_request.length());
                    responseBuffer.put(bad_request.getBytes());
                    responseBuffer.flip();
                    socketChannel.write(responseBuffer);
                } else {
                    String timeStr = Calendar.getInstance().getTime().toLocaleString();
                    ByteBuffer responseBuffer = ByteBuffer.allocate(timeStr.length());
                    responseBuffer.put(timeStr.getBytes());
                    responseBuffer.flip();
                    socketChannel.write(responseBuffer);
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static void main(String[] args) throws IOException {
        TimeServer timeServer = new TimeServer();
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.configureBlocking(false);
        serverSocketChannel.socket().bind(new InetSocketAddress(9999));

        while (true) {
            SocketChannel socketChannel = serverSocketChannel.accept();
            if (socketChannel == null) {
                continue;
            }
            socketChannel.configureBlocking(false);
            timeServer.idleQueue.add(socketChannel);
        }
    }
}
