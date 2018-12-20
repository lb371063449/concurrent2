package com.rinbo.netty.buf;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;

public class AllocatorTest {

    public static void main(String[] args) {
//        System.setProperty("io.netty.allocator.maxOrder","4");
        //1 分配一个ByteBuf，然后写入数据到此Buf
        PooledByteBufAllocator allocator = new PooledByteBufAllocator(false);
//        allocator.heapBuffer(16777218);
//        ByteBuf byteBuf = allocator.heapBuffer(10000);
//        allocator.heapBuffer(16777218);
        ByteBuf byteBuf1 = allocator.heapBuffer(64);
//        byteBuf1.release();
        ByteBuf byteBuf2 = allocator.heapBuffer(65);
        allocator.heapBuffer(85);
        ByteBuf byteBuf100 = allocator.heapBuffer(100);
        byteBuf100.release();
        ByteBuf byteBuf128 = allocator.heapBuffer(128);
        byteBuf128.release();
        allocator.heapBuffer(513);
        allocator.heapBuffer(8193);
        ByteBuf hugeBuf = allocator.heapBuffer(16777217);
        hugeBuf.release();
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
