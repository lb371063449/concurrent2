package com.rinbo.nio;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

public class NIODemo {

    public static void bufferDemo() {
        RandomAccessFile file = null;
        try {
            file = new RandomAccessFile("D:\\work\\workspace\\javabase\\src\\main\\java\\com\\rinbo\\javabase\\io\\nio\\NIODemo.java","rw");
            FileChannel fileChannel =  file.getChannel();
            //1、分配空间
            ByteBuffer buffer = ByteBuffer.allocate(1024);
            //2、写入数据到Buffe
            int read = fileChannel.read(buffer);
            System.out.println(read);
            while (read != -1) {
                //3、调用filp()方法
                buffer.flip();
                while (buffer.hasRemaining()) {
                    //4、从Buffer中读取数据
                    System.out.print((char) buffer.get());
                }
                //5、调用clear()方法或者compact()方法
                buffer.compact();
                read = fileChannel.read(buffer);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try{
                if(file != null){
                    file.close();
                }
            }catch (IOException e){
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        bufferDemo();
    }

    public static void m2() {
        RandomAccessFile file = null;
        try {
            file = new RandomAccessFile("D:\\work\\workspace\\javabase\\src\\main\\java\\com\\rinbo\\javabase\\io\\nio\\NIODemo.java","rw");
            FileChannel fileChannel =  file.getChannel();
            ByteBuffer buffer = ByteBuffer.allocate(1024);
            int read = fileChannel.read(buffer);
            System.out.println(read);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        }
    }

}
