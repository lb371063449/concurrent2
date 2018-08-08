package com.rinbo.netty.buf;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

public class TestByteBuf2 {

    public static void main(String[] args) {
        ByteBuf byteBuf = Unpooled.buffer(256);
        String content = "wojiushimogui";
        //写入
        byteBuf.writeBytes(content.getBytes());
        System.out.println("1、readerIndex:"+ byteBuf.readerIndex());
        System.out.println("1、readerIndex:"+ byteBuf.writerIndex());

        int len = 5;
        byte[] readContent = new byte[len];
        ByteBuf buf = byteBuf.readBytes(readContent);
        System.out.println(new String (readContent));
        System.out.println("2、readerIndex:"+ byteBuf.readerIndex());
        System.out.println("2、readerIndex:"+ byteBuf.writerIndex());

        //3 重用缓存区
        byteBuf.discardReadBytes();
        System.out.println("3、readerIndex:"+byteBuf.readerIndex());
        System.out.println("3、writerIndex:"+byteBuf.writerIndex());
    }
}
