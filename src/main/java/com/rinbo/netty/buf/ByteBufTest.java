package com.rinbo.netty.buf;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.CompositeByteBuf;
import io.netty.buffer.Unpooled;
import org.junit.Test;

import java.nio.charset.Charset;
import java.util.Iterator;

public class ByteBufTest {

    public static void main(String[] args) {
        ByteBuf byteBuf = Unpooled.buffer(10);
        String content = "wojiushimogui";
        //写入
        byteBuf.writeBytes(content.getBytes());
        System.out.println("1、readerIndex:" + byteBuf.readerIndex());
        System.out.println("1、readerIndex:" + byteBuf.writerIndex());

        int len = 5;
        byte[] readContent = new byte[len];
        ByteBuf buf = byteBuf.readBytes(readContent);
        System.out.println(new String(readContent));
        System.out.println("2、readerIndex:" + byteBuf.readerIndex());
        System.out.println("2、readerIndex:" + byteBuf.writerIndex());

        //3 重用缓存区
        byteBuf.discardReadBytes();
        System.out.println("3、readerIndex:" + byteBuf.readerIndex());
        System.out.println("3、writerIndex:" + byteBuf.writerIndex());
    }

    @Test
    public void testComposite() {
        //组合缓冲区
        CompositeByteBuf compBuf = Unpooled.compositeBuffer();
        //堆缓冲区
        ByteBuf heapBuf = Unpooled.buffer(8);
        //直接缓冲区
        ByteBuf directBuf = Unpooled.directBuffer(16);
        //添加ByteBuf到CompositeByteBuf
        compBuf.addComponents(heapBuf, directBuf);
        //删除第一个ByteBuf
        compBuf.removeComponent(0);
        Iterator<ByteBuf> iter = compBuf.iterator();
        while (iter.hasNext()) {
            System.out.println(iter.next().toString());
        }

        //使用数组访问数据
        if (!compBuf.hasArray()) {
            int len = compBuf.readableBytes();
            byte[] arr = new byte[len];
            compBuf.getBytes(0, arr);
        }
    }

    @Test
    public void testSlice() {
        Charset utf8 = Charset.forName("UTF-8");
        //创建一个ByteBuf
        ByteBuf buf = Unpooled.copiedBuffer("Netty in Action rocks!", utf8);
        //slice不会创建新的byte[]
        ByteBuf sliced = buf.slice(0, 14);
        //输出 Netty in Actio
        System.out.println(sliced.toString(utf8));
        buf.setByte(0, (byte)'J');
        //输出 Jetty in Actio
        System.out.println(sliced.toString(utf8));
        //输出 Jetty in Actio rocks!
        System.out.println(buf.toString(utf8));
    }

    @Test
    public void testCopy() {
        Charset utf8 = Charset.forName("UTF-8");
        //创建一个ByteBuf
        ByteBuf buf = Unpooled.copiedBuffer("Netty in Action rocks!", utf8);
        //copy创建新的byte[]
        ByteBuf copy = buf.copy(0, 14);
        //输出 Netty in Actio
        System.out.println(copy.toString(utf8));
        buf.setByte(0, (byte)'J');
        //输出 Netty in Actio
        System.out.println(copy.toString(utf8));
        //Jetty in Action rocks!
        System.out.println(buf.toString(utf8));
    }
}
