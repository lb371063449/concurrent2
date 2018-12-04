package com.rinbo.io;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Scanner;

/**
 * @author Rinbo
 */
@Slf4j
public class Scanner_5 {

    //Scan a File
    @Test
    public void readFileWithScanner() throws IOException {
        Scanner scanner = new Scanner(new File("D:\\work\\workspace\\foundation\\src\\main\\java\\com\\rinbo\\io\\Scanner_5.java"));
        while (scanner.hasNext()) {
            log.debug(scanner.nextLine());
        }
        scanner.close();
    }

    //BufferedReader when we want to read the input into lines
    //Scanner to read the input into tokens，根据useDelimiter来分割
    @Test
    public void convertInputStreamToString() throws IOException {

        FileInputStream inputStream = new FileInputStream("d:\\test2.txt");
        Scanner scanner = new Scanner(inputStream);
        scanner.useDelimiter(", ");
        log.debug(scanner.next());
        // nextLine读完内容后，Scanner会将其位置设置为下一行的开头。
        // 需要记住的重点是nextLine使用行分隔符并将扫描程序的位置移动到下一行
//        log.debug(scanner.nextLine());
        scanner.close();
    }

    //Scan a String
    @Test
    public void scanString() throws IOException {
        String input = "Hello 1 F 3.5";
        Scanner scanner = new Scanner(input);

        log.debug(scanner.next());
        log.debug(scanner.nextInt()+"");
        //The method nextInt(16) reads the next token as a hexadecimal integer value
        log.debug(scanner.nextInt(16)+"");
        log.debug(scanner.nextDouble()+"");

        scanner.close();
    }

    //Find Pattern
    @Test
    public void findPattern() throws IOException {
        FileInputStream inputStream = new FileInputStream("d:\\test2.txt");
        Scanner scanner = new Scanner(inputStream);
        String inLine = scanner.findWithinHorizon("wo..d", 5);
        log.debug(inLine);

        String result = scanner.findWithinHorizon("wo..d", 3);
        log.debug(result);
    }

    //Change Scanner Delimiter
    @Test
    public void changeScannerDelimiter() throws IOException {
        FileInputStream inputStream = new FileInputStream("d:\\test2.txt");
        Scanner scanner = new Scanner(inputStream);
        scanner.useDelimiter("o");
        //可以指定多个
//        scanner.useDelimiter(",|-");
        while (scanner.hasNext()) {
            log.debug(scanner.next());
        }
    }
}
