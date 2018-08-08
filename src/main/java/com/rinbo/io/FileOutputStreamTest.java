package com.rinbo.io;

import org.junit.Test;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

public class FileOutputStreamTest {

    @Test
    public void testOutput() throws IOException {
        long currentSize =  65536 * 1024;
        ByteBuffer fill = ByteBuffer.allocateDirect(1);
        byte a = 'a';
        fill.put(a);
        FileOutputStream f = new FileOutputStream("e:\\a.txt");
        fill.position(0);
        //将fill写入到文件的currentSize-fill.remaining()位置，中间补0
        f.getChannel().write(fill, currentSize-fill.remaining());
    }
}
