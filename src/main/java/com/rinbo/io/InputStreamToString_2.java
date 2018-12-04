package com.rinbo.io;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Scanner;
import java.util.UUID;

/**
 * how to convert an InputStream to a String
 *
 * @author rinbo
 */

@Slf4j
public class InputStreamToString_2 {

    //Converting with Java – InputStream
    @Test
    public void usingJava5_convertingAnInputStreamToAString()
            throws IOException {
        String originalString = "hello word !";
        InputStream inputStream = new ByteArrayInputStream(originalString.getBytes());
        StringBuilder textBuilder = new StringBuilder();
        Reader reader = new BufferedReader(new InputStreamReader(inputStream, Charset.forName(StandardCharsets.UTF_8.name())));
        int c = 0;
        while ((c = reader.read()) != -1) {
            textBuilder.append((char) c);
        }

        log.debug(textBuilder.toString());
    }

    //Converting with Java and a Scanner
    @Test
    public void usingJava7_convertingAnInputStreamToAString() {
        String originalString = "hello word !\\n" +
                "my name is rinbo";
        InputStream inputStream = new ByteArrayInputStream(originalString.getBytes());

        String text = null;

        Scanner scanner = new Scanner(inputStream, StandardCharsets.UTF_8.name());
        text = scanner.useDelimiter("\\A").next();
        log.debug(text);
    }

    //Converting Using ByteArrayOutputStream
    @Test
    public void usingByteArrayOutputStream_convertingAnInputStreamToAString() throws IOException {
        String originalString = "hello word !,my name is rinbo";
        InputStream inputStream = new ByteArrayInputStream(originalString.getBytes());

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        int nRead;
        byte[] data = new byte[1024];
        while ((nRead = inputStream.read(data, 0, data.length)) != -1) {
            outputStream.write(data,0,nRead);
        }

        //记得flush
        outputStream.flush();
        byte[] byteArray = outputStream.toByteArray();
        log.debug(new String(byteArray));
    }

    //Converting with java.nio
    @Test
    public void usingNIOFiles_convertingAnInputStreamToAString() throws IOException {
        String originalString = "hello word !,my name is rinbo";
        InputStream inputStream = new ByteArrayInputStream(originalString.getBytes());
        //using the java.nio.file.Files class to create a temporary file
        Path path = Files.createTempDirectory("").resolve(UUID.randomUUID().toString() + ".tmp");
        //copy the content of the InputStream to the file
        Files.copy(inputStream,path, StandardCopyOption.REPLACE_EXISTING);
        //convert the file content to a String with the readAllBytes() method
        byte[] bytes = Files.readAllBytes(path);
        log.debug(new String(bytes));
    }
}
