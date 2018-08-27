package com.rinbo.bit;

import org.junit.Test;

import java.util.Arrays;

public class BitTest {

    public static void main(String[] args) {
        //只要根据最未位是0还是1来决定，为0就是偶数，为1就是奇数
//        for (int i = 0; i < 100; i ++) {
//            if ((i & 1) == 0) { // 偶数
//                System.out.println(i);
//            }
//        }

        /**
         *  第一步 a^=b 即a=(a^b)； 第二步 b^=a 即b=b^(a^b)，由于^运算满足交换律，b^(a^b)=b^b^a。
         *  由于一个数和自己异或的结果为0并且任何数与0异或都会不变的，所以此时b被赋上了a的值；
         *  第三步 a^=b 就是a=a^b，由于前面二步可知a=(a^b)，b=a，所以a=a^b即a=(a^b)^a。故a会被赋上b的值
         */
        int c = 1, d = 2;
        c ^= d;
        d ^= c;
        c ^= d;
        System.out.println("c=" + c);
        System.out.println("d=" + d);

        //先取反，再加1
        int a = -15, b = 15;
        System.out.println(~a + 1);
        System.out.println(~b + 1);

        //对于任何数，与0异或都会保持不变，与-1异或就相当于取反。
        //因此，a与i异或后再减i（因为i为0或-1，所以减i即是要么加0要么加1）也可以得到绝对值
        int j = a >> 31;
        System.out.println((a ^ j) - j);

        int max = 100;
        boolean[] flags = new boolean[max];
        int [] primes = new int[max / 3 + 1];
        int pi = 0;

        for (int m = 2; m < max ; m ++) {
            if (!flags[m]) {
                primes[pi++] = m;
                for(int n = m; n < max; n += m) {
                    flags[n] = true;
                }
            }
        }

        System.out.println(Arrays.toString(primes));
    }


    @Test
    public void test2CF() {
        System.out.println(get2CF(2222));
    }

    /**
     * 返回大于value的最小2的x方的数
     */
    private long get2CF(long value) {
        value --;
        value |= value >>>  1;
        value |= value >>>  2;
        value |= value >>>  4;
        value |= value >>>  8;
        value |= value >>> 16;
        value ++;
        return value;
    }

    /**
     * 判断一个数是否小于2的n次方
     * @return
     */
    private boolean compare2CF(long value) {
        //2048是2的11次方
        return (value & -2048) == 0;
    }

    /**
     * 2的n次方的负数
     * @param value
     * @return
     */
    private long getFS(long value) {
        return  ~(value - 1);
    }

    //2的对数
    private static int log2(int val) {
        return (Integer.SIZE - 1) - Integer.numberOfLeadingZeros(val);
    }
}
