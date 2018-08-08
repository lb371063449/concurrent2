package com.rinbo.netty.buf;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;

public class TestByteBuf {

    public static void main(String[] args) {
//        System.setProperty("io.netty.allocator.maxOrder","4");
        //1 分配一个ByteBuf，然后写入数据到此Buf
        PooledByteBufAllocator allocator = new PooledByteBufAllocator(false);
        ByteBuf byteBuf = allocator.heapBuffer(511);
        byteBuf.release();
        ByteBuf byteBuf1 = allocator.heapBuffer(511);

        String content = "wojiushimogui";
        byteBuf.writeBytes(content.getBytes());
        System.out.println("1、readerIndex:"+byteBuf.readerIndex());
        System.out.println("1、writerIndex:"+byteBuf.writerIndex());
    }
}
