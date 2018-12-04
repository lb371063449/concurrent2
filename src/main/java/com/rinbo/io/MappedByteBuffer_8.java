package com.rinbo.io;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.nio.CharBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.EnumSet;

@Slf4j
public class MappedByteBuffer_8 {

    //加载一个文件
    Path getFileURIFromResources(String fileName) throws Exception {
        ClassLoader classLoader = getClass().getClassLoader();
        return Paths.get(fileName);
    }

    @Test
    public void read() throws Exception {
        CharBuffer charBuffer = null;
        Path pathToRead = getFileURIFromResources("d:\\test2.txt");
        //1、firstly we need to create a FileChannel from it，调用newByteChannel
        try (FileChannel fileChannel =
                     (FileChannel)Files.newByteChannel(pathToRead, EnumSet.of(StandardOpenOption.READ))) {

            MappedByteBuffer mappedByteBuffer =
                    //指定MapMode、读取的开始位置、读取的长度
                    fileChannel.map(FileChannel.MapMode.READ_ONLY, 8, fileChannel.size()-8);

            if (mappedByteBuffer != null) {
                //when we call the decode() method passing MappedByteBuffer,
                //we read from memory, not from the disc.
                //直接从内存中读取
                charBuffer = Charset.forName("UTF-8").decode(mappedByteBuffer);
            }
        }
        log.debug(charBuffer.toString());
    }

    @Test
    public void write() throws Exception {
        CharBuffer charBuffer = CharBuffer.wrap("This will be written to the file");
        Path pathToRead = getFileURIFromResources("d:\\test2.txt");

        try (FileChannel fileChannel =
                     (FileChannel)Files.newByteChannel(pathToRead, EnumSet.of(StandardOpenOption.READ,
                             StandardOpenOption.WRITE,
                             StandardOpenOption.TRUNCATE_EXISTING))) {
            MappedByteBuffer mappedByteBuffer = fileChannel.map(FileChannel.MapMode.READ_WRITE, 0, charBuffer.length());

            if (mappedByteBuffer != null) {
                mappedByteBuffer.put(Charset.forName("utf-8").encode(charBuffer));
            }

        }
    }
}
