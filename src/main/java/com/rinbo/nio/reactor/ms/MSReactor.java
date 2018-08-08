package com.rinbo.nio.reactor.ms;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 主从式Reactor
 */
public class MSReactor {

    //主Reactor
    static class TCPReactor implements Runnable {

        private final ServerSocketChannel ssc;
        private final Selector selector;

        TCPReactor(int port) throws IOException {
            selector = Selector.open();
            ssc = ServerSocketChannel.open();
            InetSocketAddress addr = new InetSocketAddress(port);
            ssc.socket().bind(addr);
            ssc.configureBlocking(false);
            SelectionKey sk = ssc.register(selector, SelectionKey.OP_ACCEPT);
            sk.attach(new Acceptor(ssc));
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
                Set<SelectionKey> selectedKeys = selector.selectedKeys();
                Iterator<SelectionKey> it = selectedKeys.iterator();
                while (it.hasNext()) {
                    SelectionKey key = it.next();
                    dispatch(key);
                    it.remove();
                }
            }
        }

        private void dispatch(SelectionKey key) {
            Runnable r = (Runnable) (key.attachment());
            if (r != null) {
                r.run();
            }
        }
    }

    static class Acceptor implements Runnable {

        private final ServerSocketChannel ssc;
        private final int cores = Runtime.getRuntime().availableProcessors();
        private final Selector[]
                selectors = new Selector[cores];
        private int selIdx = 0;
        private Thread[] t = new Thread[cores];
        private TCPSubReactor[] r = new TCPSubReactor[cores];

        Acceptor(ServerSocketChannel ssc) throws IOException {
            this.ssc = ssc;
            for (int i = 0; i < cores; i++) {
                selectors[i] = Selector.open();
                r[i] = new TCPSubReactor(selectors[i]);
                t[i] = new Thread(r[i]);
                t[i].start();
            }
        }

        @Override
        public void run() {
            try {
                //接收客户端连接
                SocketChannel sc = ssc.accept();
                if (sc != null) {
                    sc.configureBlocking(false);
                    r[selIdx].setRestart(true);
                    //调用selector的wakeup()函数唤醒阻塞住的selector，以注册新IO事件后再继续监听
                    //由于调用selector.select()的线程可能会因为循环太快，导致selector被唤醒后再度于IO事件成功注册前被调用selector.select()而阻塞住，
                    //因此给TCPSubReactor线程循环设置一个flag来控制，
                    selectors[selIdx].wakeup();
                    //注册客户端处理事件，注册到从Reactor
                    SelectionKey sk = sc.register(selectors[selIdx], SelectionKey.OP_READ);
                    selectors[selIdx].wakeup();
                    sk.attach(new TCPHandler(sk, sc));
                    r[selIdx].setRestart(false);
                    if (++selIdx == selectors.length) {
                        selIdx = 0;
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    //从Reactor
    static class TCPSubReactor implements Runnable {
        private final Selector selector;
        private boolean restart = false;

        public TCPSubReactor(Selector selector) {
            this.selector = selector;
        }

        @Override
        public void run() {
            while (!Thread.interrupted()) {
                while (!restart) {
                    try {
                        if (selector.select() == 0) {
                            continue;
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Set<SelectionKey> selectedKeys = selector.selectedKeys();
                    Iterator<SelectionKey> it = selectedKeys.iterator();
                    while (it.hasNext()) {
                        dispatch(it.next());
                        it.remove();
                    }
                }
            }
        }

        private void dispatch(SelectionKey key) {
            Runnable r = (Runnable) (key.attachment());
            if (r != null) {
                r.run();
            }
        }


        public void setRestart(boolean restart) {
            this.restart = restart;
        }
    }

    static class TCPHandler implements Runnable {

        private final SelectionKey sk;
        private final SocketChannel sc;
        private static final int THREAD_COUNTING = 10;
        private static ThreadPoolExecutor pool = new ThreadPoolExecutor(THREAD_COUNTING, THREAD_COUNTING, 10, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());
        private HandlerState state;

        public TCPHandler(SelectionKey sk, SocketChannel sc) {
            this.sk = sk;
            this.sc = sc;
            state = new ReadState();
        }

        @Override
        public void run() {
            try {
                state.handle(this, sk, sc, pool);
            } catch (IOException e) {
                e.printStackTrace();
                closeChannel();
            }
        }

        public void closeChannel() {
            try {
                sk.cancel();
                sc.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }

        public void setState(HandlerState state) {
            this.state = state;
        }

    }

    interface HandlerState {
        void changeState(TCPHandler h);

        void handle(TCPHandler h, SelectionKey sk, SocketChannel sc, ThreadPoolExecutor pool) throws IOException;
    }

    static class ReadState implements HandlerState {

        @Override
        public void changeState(TCPHandler h) {
            h.setState(new WorkState());
        }

        @Override
        public void handle(TCPHandler h, SelectionKey sk, SocketChannel sc, ThreadPoolExecutor pool) throws IOException {
            byte[] arr = new byte[1024];
            ByteBuffer buf = ByteBuffer.wrap(arr);
            int numBytes = sc.read(buf);
            if (numBytes == -1) {
                h.closeChannel();
                return;
            }
            String str = new String(arr);
            if ((str != null) && !str.equals(" ")) {
                h.setState(new WorkState());
                pool.execute(() -> process(h, sk, str));
            }
        }

        synchronized void process(TCPHandler h, SelectionKey sk, String str) {
            System.out.println(Thread.currentThread().getName() + "-" + str);
            changeState(h);
            sk.interestOps(SelectionKey.OP_WRITE);
            sk.selector().wakeup();
        }
    }

    static class WorkState implements HandlerState {

        @Override
        public void changeState(TCPHandler h) {
            h.setState(new ReadState());
        }

        @Override
        public void handle(TCPHandler h, SelectionKey sk, SocketChannel sc, ThreadPoolExecutor pool) throws IOException {
            String str = "Your message has sent to " + sc.socket().getLocalSocketAddress().toString() + "\r\n";
            // wrap自动把buf的position设为0，所以不需要再flip()
            ByteBuffer buf = ByteBuffer.wrap(str.getBytes());

            while (buf.hasRemaining()) {
                sc.write(buf);
            }
            h.setState(new ReadState());
            sk.interestOps(SelectionKey.OP_READ);
            sk.selector().wakeup();
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
