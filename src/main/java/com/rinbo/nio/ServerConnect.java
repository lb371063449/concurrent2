package com.rinbo.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

public class ServerConnect {
    private static final int BUF_SIZE=1024;
    private static final int PORT = 9999;
    private static final int TIMEOUT = 3000;

    public static void main(String[] args) {
        selector();
    }

    private static void selector() {
        Selector selector = null;
        ServerSocketChannel ssc = null;

        try {
            //1、创建ServerSocket通道和selector
            ssc = ServerSocketChannel.open();
            selector = Selector.open();
            //2、绑定监听端口
            ssc.socket().bind(new InetSocketAddress(PORT));
            //3、配置非阻塞
            ssc.configureBlocking(false);
            //4、将selector注册到想要监控的信道上，此处为ServerSocketChannel
            //与Selector一起使用时，Channel必须处于非阻塞模式下
            //register()方法的第二个参数。这是一个“interest集合”，意思是在通过Selector监听Channel时对什么事件感兴趣
            ssc.register(selector,SelectionKey.OP_ACCEPT);

            while (true) {
                //5、select()返回可进行I/O操作的信道数量,
                //如果selector.select()为0，说明selector上注册的通道io没有准备好
                if (selector.select(TIMEOUT)==0) {
                    System.out.println("====");
                    continue;
                }
                //6、此时selector上注册的通道io已经准备好了,获取所有已经准备好的key
                Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
                while (iterator.hasNext()) {
                    SelectionKey key = iterator.next();
                    //8、业务处理-------------------------start
                    if (key.isAcceptable()) {
                        //8.1、处理SocketChannel连接请求
                        handleAccept(key);
                    }
                    if (key.isReadable()) {
                        //8.2、处理SocketChannel读
                        handleRead(key);
                    }
                    if (key.isWritable()) {
                        //8.3、处理SocketChannel写
                        handleWrite(key);
                    }
                    if (key.isConnectable()) {
                        System.out.println("isConnectable = true");
                    }
                    //8、业务处理-------------------------end
                    //7、移除key
                    iterator.remove();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (ssc != null) {
                    ssc.close();
                }
                if (selector != null) {
                    selector.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void handleAccept(SelectionKey key) throws IOException {
        //a、获取ServerSocketChannel
        ServerSocketChannel channel = (ServerSocketChannel) key.channel();
        //b、接收客户端请求
        SocketChannel socketChannel = channel.accept();
        //c、设置非阻塞
        socketChannel.configureBlocking(false);
        //d、将selector注册到想要监控的信道上，此处为SocketChannel
        socketChannel.register(key.selector(),SelectionKey.OP_READ,ByteBuffer.allocateDirect(BUF_SIZE));
    }
    public static void handleRead(SelectionKey key) throws IOException {
        //a、获取SocketChannel
        SocketChannel sc = (SocketChannel)key.channel();
        //b、获取缓冲中的内容
        ByteBuffer buffer = (ByteBuffer) key.attachment();
        int len = sc.read(buffer);
        if (len>0) {
            buffer.flip();
            while (buffer.hasRemaining()) {
                System.out.print((char) buffer.get());
            }
            System.out.println();
            buffer.compact();
            len = sc.read(buffer);
        }
        if (len == -1) {
            sc.close();
        }
    }
    public static void handleWrite(SelectionKey key) throws IOException {
        ByteBuffer buffer = (ByteBuffer) key.attachment();
        buffer.flip();
        //a、获取SocketChannel
        SocketChannel sc = (SocketChannel)key.channel();
        while (buffer.hasRemaining()) {
            sc.write(buffer);
        }
        buffer.compact();
    }
}
