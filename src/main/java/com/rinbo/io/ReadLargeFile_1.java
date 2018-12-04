package com.rinbo.io;

import com.google.common.base.Charsets;
import com.google.common.io.Files;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Scanner;

@Slf4j
public class ReadLargeFile_1 {

    /**
     * 文件被全部加载到内存
     *
     * @throws IOException
     */
    @Test
    public void t_readLines() throws IOException {
        List<String> strings = Files.readLines(new File("D:\\work\\workspace\\foundation\\src\\main\\java\\com\\rinbo\\io\\ReadLargeFile_1.java"), Charsets.UTF_8);
        strings.stream().forEach((i) -> log.debug(i));
    }

    //通过流的方式读取大文件
    @Test
    public void t_stream() throws IOException {
        FileInputStream inputStream = null;
        Scanner sc = null;

        try {
            inputStream = new FileInputStream("D:\\work\\workspace\\foundation\\src\\main\\java\\com\\rinbo\\io\\ReadLargeFile_1.java");
            sc = new Scanner(inputStream, "UTF-8");
            while (sc.hasNext()) {
                log.debug(sc.nextLine());
            }
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
            if (sc != null) {
                sc.close();
            }
        }
    }
}
