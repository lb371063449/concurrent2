package com.rinbo.format;

public class LongParserDemo {

    public static void main(String[] args) {
        String v = "12";
        long l = Long.parseLong(v, 16);
        System.out.println(l);
    }
}
