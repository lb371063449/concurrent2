package com.rinbo.io;

import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.PushbackInputStream;

public class PushInputStreamDemo {

    @Test
    public void testPush() throws IOException {
        String str = "www.baidu.com";
        PushbackInputStream push = null;
        ByteArrayInputStream bai = null;
        bai = new ByteArrayInputStream(str.getBytes());
        push = new PushbackInputStream(bai);
        System.out.print("读取之后的数据为：");
        int temp = 0;
        while ((temp = push.read()) != -1) {
            // 判断是否读取到了“.”
            if (temp == '.') {
                // 放回到缓冲区之中
                push.unread(temp);
                //再读一遍
                temp = push.read();
                System.out.print("（退回" + (char) temp + "）");
            } else {
                // 输出内容
                System.out.print((char) temp);
            }
        }
    }

}
