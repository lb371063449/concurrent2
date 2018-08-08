package com.rinbo.format;

import java.io.*;

public class InputStreamDemo {
    public static void main(String[] args) throws IOException {
        memoTest();
    }

    private static void encoding1() throws UnsupportedEncodingException {
        //编码
        byte[] bytes = "中".getBytes("utf-8");
        //解码
        String s = new String(bytes,"utf-8");
    }

    public static void byteTest() throws IOException {
        System.out.println(System.getProperty("file.encoding"));
        File file = new File("E:\\test.txt");
        InputStream input = new FileInputStream(file);
        StringBuffer buffer = new StringBuffer();
        byte[] bytes = new byte[1024];
        for (int n; (n = input.read(bytes)) != -1; ) {
            buffer.append(new String(bytes, 0, n));
        }
        System.out.println(buffer);
    }

    public static void charTest() throws IOException {
        String file = "E:\\test.txt";
        //写字符换转成字节流
        String charset = "UTF-8";
        FileOutputStream outputStream = new FileOutputStream(file);
        OutputStreamWriter writer = new OutputStreamWriter(outputStream, charset);
        try {
            writer.write("我是 cm");
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            writer.close();
        }
        FileInputStream inputStream = new FileInputStream(file);
        InputStreamReader reader = new InputStreamReader(inputStream, charset);
        StringBuffer buffer = new StringBuffer();
        char[] buf = new char[64];
        int count = 0;
        try {
            while ((count = reader.read(buf)) != -1) {
                buffer.append(buf, 0, count);
            }
        } finally {
            reader.close();
        }
        System.out.println(buffer);
    }

    public static void memoTest() throws UnsupportedEncodingException {
        String s = "我是 cm";
        //getBytes会调用encode方法首先调用系统的默认编码格式，如果没有指定编码格式则默认使用ISO-8859-1编码格式进行编码操作，
        byte[] bytes = s.getBytes();
        //new string 的构造函数内部是调用StringCoding.decode()方法：
        String s1 = new String(bytes, "GBK");
        String s2 = new String(bytes);
        System.out.println(s1 + "          " + s2);
    }

    public static void memotest2() throws UnsupportedEncodingException {
        String string ="我是 cm";
        printChart(string.toCharArray());
        printChart(string.getBytes("ISO-8859-1"));
        printChart(string.getBytes("GBK"));
        printChart(string.getBytes("UTF-8"));
    }

    /**
     * char转换为16进制
     */
    public static void printChart(char[] chars) {
        for (int i = 0; i < chars.length; i++) {
            System.out.print(Integer.toHexString(chars[i]) + " ");
        }
        System.out.println("");
    }

    /**
     * byte转换为16进制
     */
    public static void printChart(byte[] bytes) {
        for (int i = 0; i < bytes.length; i++) {
            String hex = Integer.toHexString(bytes[i] & 0xFF);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }
            System.out.print(hex.toUpperCase() + " ");
        }
        System.out.println("");
    }
}
