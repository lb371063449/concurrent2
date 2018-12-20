package com.rinbo.io;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * write to a file using Java
 *
 * @author Rinbo
 */
@Slf4j
public class WriteToFile_4 {

    //1. Write with BufferedWriter
    @Test
    public void usingBufferedWritter()
            throws IOException {
        String str = "Hello";
        BufferedWriter writer = new BufferedWriter(new FileWriter("d:\\test.txt"));
        writer.write(str);
        writer.close();

        String str2 = "World";
        //append
        BufferedWriter writer2 = new BufferedWriter(new FileWriter("d:\\test.txt", true));
        writer2.append(' ');
        writer2.append(str);
        writer2.close();
    }

    //2. Write with PrintWriter
    @Test
    public void usingPrintWriter()
            throws IOException {
        PrintWriter printWriter = new PrintWriter(new FileWriter("d:\\test.txt"));
        printWriter.print("Some String");
        printWriter.printf("Product name is %s and its price is %d $", "iPhone", 1000);
        printWriter.close();
    }

    //3. Write with FileOutputStream
    @Test
    public void usingFileOutputStream()
            throws IOException {
        FileOutputStream outputStream = new FileOutputStream(new File("d:\\test.txt"));
        outputStream.write("hello word".getBytes());
        outputStream.close();
    }

    //4. Write with DataOutputStream
    @Test
    public void usingDataOutputStream()
            throws IOException {
        DataOutputStream outputStream = new DataOutputStream(new FileOutputStream(new File("d:\\test.txt")));
        outputStream.write("hello word".getBytes());
        outputStream.close();
    }

    //5. Write with RandomAccessFile
    @Test
    public void usingRandomAccessFile()
            throws IOException {
        // RandomAccessFile enable us to write at a specific position in the file given the offset
        // from the beginning of the file – in bytes
        //RandomAccessFile操作的是字节

        int data1 = 1;
        int data2 = 2;

        writeToPosition("d:\\test.txt", data1, 2);
//        log.debug((char) readFromPosition("d:\\test.txt", 2) + "");

//        writeToPosition("d:\\test.txt", data2, 4);
//        log.debug(readFromPosition("d:\\test.txt", 4) + "");
    }

    private void writeToPosition(String filename, int data, long position)
            throws IOException {
        RandomAccessFile writer = new RandomAccessFile(filename, "rw");
        //定位到写的位置
        writer.seek(position);
        //写4个字节(int 4个字节)
        writer.writeInt(data);
        writer.writeChar(1);
        writer.close();
    }

    @Test
    public void randomAccessFile()
            throws IOException {
        RandomAccessFile writer = new RandomAccessFile("d:\\test.txt", "rw");
        //定位到写的位置
        byte b = 2;
        writer.seek(0);
//        writer.writeByte(b);
        writer.writeInt(12);
        writer.close();
    }


    private byte readFromPosition(String filename, long position)
            throws IOException {
        byte result = 0;
        RandomAccessFile reader = new RandomAccessFile(filename, "r");
        //定位到读的位置
        reader.seek(position);
        //读4个字节(int 4个字节)
//        result = reader.readInt();
        result = reader.readByte();
        reader.close();
        return result;
    }

    //6.  Write with FileChannel
    @Test
    public void usingFileChannel()
            throws IOException {
        RandomAccessFile stream = new RandomAccessFile("d:\\test.txt", "rw");
        FileChannel channel = stream.getChannel();
        String value = "Hello";
        byte[] strBytes = value.getBytes();
        ByteBuffer byteBuffer = ByteBuffer.allocate(strBytes.length);
        byteBuffer.put(strBytes);
        byteBuffer.flip();

        channel.write(byteBuffer);

        stream.close();
        channel.close();
    }

    // 7. Write to file using Java 7
    // Using the Files class, we can create, move, copy, delete files and directories as well
    @Test
    public void usingFiles()
            throws IOException {
        String str = "Hello";
        Path path = Paths.get("d:\\test.txt");
        Files.write(path,str.getBytes());
        String read = Files.readAllLines(path).get(0);
        log.debug(read);
    }

    // 8. Write to temporary file
    @Test
    public void usingTemporaryFiles()
            throws IOException {
        String toWrite = "Hello";
        File tmpFile = File.createTempFile("test", ".tmp");
        FileWriter writer = new FileWriter(tmpFile);
        writer.write(toWrite);
        writer.close();

        BufferedReader reader = new BufferedReader(new FileReader(tmpFile));
        log.debug(reader.readLine());
        reader.close();
    }

    // 9. Lock File Before Writing
    @Test
    public void tryToLockFile()
            throws IOException {
        RandomAccessFile stream = new RandomAccessFile("d:\\test.txt", "rw");
        FileChannel channel = stream.getChannel();
        FileLock lock = null;
        try {
            lock = channel.tryLock();
        } catch (IOException e) {
            stream.close();
            channel.close();
        }
        stream.writeChars("my name is rinbo!");
        //必须释放锁
        lock.release();
        stream.close();
        channel.close();
    }
}
