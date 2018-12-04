package com.rinbo.io;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.concurrent.atomic.AtomicLong;

/**
 * how to get the size of a folder in Java
 */
@Slf4j
public class DirectorySize_6 {

    private long getFolderSize(File folder) {
        long length = 0;
        File[] files = folder.listFiles();
        int count = files.length;
        for (int i = 0; i < count; i++) {
            if (files[i].isFile()) {
                length += files[i].length();
            } else {
                length += getFolderSize(files[i]);
            }
        }
        return length;
    }

    @Test
    public void folderSizeRecursive() {
        File folder = new File("D:\\work\\workspace\\foundation");
        long size = getFolderSize(folder);
        log.debug(size + "");
    }

    //With Java 7
    @Test
    public void walkFileTree() throws IOException {
        Path path = Paths.get("D:\\work\\workspace\\foundation");
        AtomicLong size = new AtomicLong(0);
        Files.walkFileTree(path, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                size.addAndGet(attrs.size());
                return FileVisitResult.CONTINUE;
            }
        });
        log.debug(size.longValue() + "");
    }

    //With Java 8
    @Test
    public void usingJava8() throws IOException {
        Path path = Paths.get("D:\\work\\workspace\\foundation");
        long sum = Files.walk(path)
                .filter(p -> p.toFile().isFile())
                .mapToLong(p -> p.toFile().length())
                .sum();
        log.debug(sum + "");
    }
}
