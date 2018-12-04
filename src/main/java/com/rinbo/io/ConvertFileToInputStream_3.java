package com.rinbo.io;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.io.*;

/**
 *  how to convert a File to an InputStream
 * @author rinbo
 */
@Slf4j
public class ConvertFileToInputStream_3 {

    //using a FileInputStream:
    @Test
    public void useFileInputStream_convertingFileToInputStream()
            throws IOException {
        File initialFile = new File("src/main/resources/sample.txt");
        InputStream targetStream = new FileInputStream(initialFile);
    }

    //use DataInputStream to read binary or primitive data from a file:
    @Test
    public void useDataInputStream_convertingFileToInputStream()
            throws IOException {
        File initialFile = new File("src/main/resources/sample.txt");
        DataInputStream inputStream = new DataInputStream(new FileInputStream(initialFile));
    }

    //useing SequenceInputStream to concatenate input stream of two files to a single InputStream:
    @Test
    public void useSequenceInputStream_convertingFileToInputStream()
            throws IOException {
        final File initialFile = new File("D:\\work\\workspace\\foundation\\src\\main\\java\\com\\rinbo\\io\\ConvertFileToInputStream_3.java");
        final File anotherFile = new File("D:\\work\\workspace\\foundation\\src\\main\\java\\com\\rinbo\\io\\InputStreamToString_2.java");
        final InputStream targetStream = new FileInputStream(initialFile);
        final InputStream anotherTargetStream = new FileInputStream(anotherFile);

        InputStream sequenceTargetStream = new SequenceInputStream(targetStream, anotherTargetStream);
    }

}
