package com.rinbo.date;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Slf4j
public class SimpleDateUtil {

    // 长日期格式
    public static String TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";


    public static long convert2long(String date) throws ParseException {
        String format = SimpleDateUtil.TIME_FORMAT;
        SimpleDateFormat sf = new SimpleDateFormat(format);
        return sf.parse(date).getTime();
    }

    /**
     * 将长整型数字转换为日期格式的字符串
     * @param time
     * @return
     */
    public static String convert2String(long time) {
        String format = SimpleDateUtil.TIME_FORMAT;
        SimpleDateFormat sf = new SimpleDateFormat(format);
        Date date = new Date(time);
        return sf.format(date);

    }

    public static long currentSeconds() {
        return System.nanoTime();
    }


    @Test
    public void test() {
        log.debug("结果是：{}",System.currentTimeMillis());
        log.debug("结果是：{}",convert2String(561780789070160L));
    }


}