package com.rinbo.nio.reactor.single;

import lombok.AllArgsConstructor;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

/**
 * 1、Reactor：负责派发IO事件给对应的角色处理
 * 2、Acceptor：负责接受client的连线，然后给client绑定一个Handler并注册IO事件到Reactor上监听
 * 3、Handler：负责处理与client交互的事件或行为
 */
public class SingleReactor {

    static class TCPReactor implements Runnable {

        private final ServerSocketChannel ssc;
        private final Selector selector;

        TCPReactor(int port) throws IOException {
            selector = Selector.open();
            ssc = ServerSocketChannel.open();
            InetSocketAddress addr = new InetSocketAddress(port);
            // 在ServerSocketChannel绑定监听端口
            ssc.socket().bind(addr);
            // 设置ServerSocketChannel为非阻塞
            ssc.configureBlocking(false);
            // ServerSocketChannel向selector注册一个P_ACCEPT事件，然后返回该通道的key
            SelectionKey sk = ssc.register(selector, SelectionKey.OP_ACCEPT);
            // 给定key一个附加的Acceptor对象
            sk.attach(new Acceptor(selector, ssc));
        }

        @Override
        public void run() {
            for (; ; ) {
                try {
                    int select = selector.select();
                    if (select == 0) {
                        continue;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                // 取得所有已经就绪事件的key集合
                Set<SelectionKey> selectedKeys = selector.selectedKeys();
                Iterator<SelectionKey> it = selectedKeys.iterator();
                while (it.hasNext()) {
                    // 根据事件的key進行调度
                    SelectionKey key = it.next();
                    dispatch(key);
                    it.remove();
                }
            }
        }

        private void dispatch(SelectionKey key) {
            // 调用事件的key绑定的对象的run()
            Runnable r = (Runnable) (key.attachment());
            if (r != null) {
                r.run();
            }
        }
    }

    @AllArgsConstructor
    static class Acceptor implements Runnable {

        private Selector selector;
        private ServerSocketChannel ssc;

        @Override
        public void run() {
            try {
                // 接受client连接请求
                SocketChannel accept = ssc.accept();
                accept.configureBlocking(false);
                SelectionKey key = accept.register(selector, SelectionKey.OP_READ);
                // 使一个阻塞的selector操作立即返回
                selector.wakeup();
                // 给定key一个附加的TCPHandler对象
                key.attach(new TCPHandler(accept,key,0));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @AllArgsConstructor
    static class TCPHandler implements Runnable {

        private SocketChannel socketChannel;
        private SelectionKey key;
        private int state;

        @Override
        public void run() {
            try {
                if (state == 0) {
                    read();
                } else {
                    send();
                }
            } catch (IOException e) {
                closeChannel();
            }
        }
        private void closeChannel() {
            try {
                key.cancel();
                socketChannel.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
        private synchronized void read() throws IOException {
            byte[] arr = new byte[1024];
            ByteBuffer buf = ByteBuffer.wrap(arr);
            int numBytes = socketChannel.read(buf);
            if(numBytes == -1) {
                closeChannel();
                return;
            }

            //将读取到的byte内容转换为字符串
            String str = new String(arr);
            if ((str != null) && !str.equals(" ")) {
                //逻辑处理
                process(str);
                //改变状态
                state = 1;
                // 通过key改变通道注册的事件
                key.interestOps(SelectionKey.OP_WRITE);
                // 使一个阻塞的selector操作立即返回
                key.selector().wakeup();
            }

        }
        private synchronized void send() throws IOException {
            String str = "Your message has sent to "
                    + socketChannel.socket().getLocalSocketAddress().toString() + "\r\n";
            ByteBuffer buf = ByteBuffer.wrap(str.getBytes());
            while (buf.hasRemaining()) {
                // 回传给client回应字符串，发送buf的position位置 到limit位置为止之间的內容
                socketChannel.write(buf);
            }
            //改变状态
            state = 0;
            // 通过key改变通道注册的事件
            key.interestOps(SelectionKey.OP_READ);
            // 使一个阻塞的selector操作立即返回
            key.selector().wakeup();

        }
        void process(String str) {
            System.out.println(Thread.currentThread().getName() + "-" + str);
        }
    }

    public static void main(String[] args) {
        try {
            TCPReactor reactor = new TCPReactor(9999);
            reactor.run();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
