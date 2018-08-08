package com.rinbo.io;

import org.junit.Test;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.Adler32;
import java.util.zip.CheckedInputStream;
import java.util.zip.CheckedOutputStream;

//CheckedInputStream和CheckedOutputStream
// 这是两个过滤流，用于维护数据校验和
public class CheckedInputStreamTest {

    @Test
    public void testOutput() throws IOException {
        FileOutputStream fs = new FileOutputStream("e:\\a.txt");
        CheckedOutputStream cos = new CheckedOutputStream(fs,new Adler32());
        cos.write("hello word".getBytes());
        System.out.println(cos.getChecksum().getValue());
    }

    @Test
    public void testInput() throws IOException {
        FileInputStream fi = new FileInputStream("e:\\a.txt");
        CheckedInputStream cis = new CheckedInputStream(fi,new Adler32());
        byte[] b = new byte[cis.available()];
        //必须要读取完成之后，校验值才可能正确
        cis.read(b);
        System.out.println(new String(b));
        System.out.println(cis.getChecksum().getValue());
    }
}
