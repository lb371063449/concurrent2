package com.rinbo.io;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * how to get the size of a file
 */
@Slf4j
public class FileSize_7 {

    private long getFileSize(File file) {
        long length = file.length();
        return length;
    }

    @Test
    public void whenGetFileSize_thenCorrect() {
        long expectedSize = 12607;

        File imageFile = new File("D:\\work\\workspace\\foundation\\src\\main\\java\\com\\rinbo\\io\\FileSize_7.java");
        long size = getFileSize(imageFile);
        log.debug(size + "");
    }

    //use the FileChannel.size() API to get the size of a file in bytes:
    @Test
    public void whenGetFileSizeUsingNioApi_thenCorrect() throws IOException {
        long expectedSize = 12607;

        Path imageFilePath = Paths.get("D:\\work\\workspace\\foundation\\src\\main\\java\\com\\rinbo\\io\\FileSize_7.java");
        FileChannel imageFileChannel = FileChannel.open(imageFilePath);
        long imageFileSize = imageFileChannel.size();
        log.debug(imageFileSize + "");
    }

}
