package com.rinbo.netty.buf;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;

public class AllocatorTest {

    public static void main(String[] args) {
//        System.setProperty("io.netty.allocator.maxOrder","4");
        //1 分配一个ByteBuf，然后写入数据到此Buf
        PooledByteBufAllocator allocator = new PooledByteBufAllocator(false);
        allocator.heapBuffer(16777218);
        ByteBuf byteBuf = allocator.heapBuffer(10000);
        allocator.heapBuffer(64);
        allocator.heapBuffer(64);
        allocator.heapBuffer(128);
        allocator.heapBuffer(64);
        allocator.heapBuffer(64);
        allocator.heapBuffer(64);
        allocator.heapBuffer(64);
        allocator.heapBuffer(64);
        allocator.heapBuffer(64);
        allocator.heapBuffer(64);
        allocator.heapBuffer(64);
        allocator.heapBuffer(16777216);
        allocator.heapBuffer(5000);
//        String content = "wojiushimogui";
//        byteBuf.writeBytes(content.getBytes());
//        System.out.println("1、readerIndex:"+byteBuf.readerIndex());
//        System.out.println("1、writerIndex:"+byteBuf.writerIndex());
//        byteBuf.release();

        allocator.directBuffer(10000);
        allocator.heapBuffer(300);
        allocator.heapBuffer(344);
        allocator.heapBuffer(466);
        allocator.heapBuffer(600);
        allocator.heapBuffer(466);
    }
}
