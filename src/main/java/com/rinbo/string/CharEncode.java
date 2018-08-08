package com.rinbo.string;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import java.io.UnsupportedEncodingException;

@Slf4j
public class CharEncode {

    @Test
    public void encode() {
        log.debug(System.getProperty("file.encoding"));
        String name = "I am 君山,淘宝";
        toHex(name.toCharArray());
        try {
            byte[] iso8859 = name.getBytes("ISO-8859-1");
            log.debug("result : {}",toHex(iso8859));
            byte[] gb2312 = name.getBytes("GB2312");
            log.debug("result : {}",toHex(gb2312));
            byte[] gbk = name.getBytes("GBK");
            log.debug("result : {}",toHex(gbk));
            byte[] utf16 = name.getBytes("UTF-16");
            log.debug("result : {}",toHex(utf16));
            byte[] utf8 = name.getBytes("UTF-8");
            log.debug("result : {}",toHex(utf8));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }


    public void toHex(char[] array) {
        for (char c : array) {
            System.out.print(Integer.toHexString(c) + " ");
        }
        System.out.println("");
    }

    private final static char[] hexArray = "0123456789ABCDEF".toCharArray();

    public static String toHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }
}
