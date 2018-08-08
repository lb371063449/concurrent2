package com.rinbo.concurrent.chapter4;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ThreadLocalParseDate {
    static ThreadLocal<SimpleDateFormat> threadLocal = new ThreadLocal<>();

    static class ParseDate implements Runnable {
        int i = 0;

        public ParseDate(int i) {
            this.i = i;
        }

        public void run() {
            try {
                SimpleDateFormat sdf = threadLocal.get();
                if (sdf == null) {
                    sdf = sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    threadLocal.set(sdf);
                }
                Date t = sdf.parse("2015-03-3 22:22:" + i%60);
                System.out.println(i + " : " + t);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        for (int i=0;i<100;i++) {
            ParseDate pd = new ParseDate(i);
            Thread t = new Thread(pd);
            t.start();
        }

    }
}
