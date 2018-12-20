package com.rinbo.collections.array;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.Arrays;

@Slf4j
public class InitializingArrays3 {

    //One Element at a Time\
    @Test
    public void onElementATime1() {
        int[] array = new int[10];
        for (int i = 0; i < array.length; i++) {
            array[i] = i + 2;
        }
    }

    //At the Time of Declaration
    @Test
    public void atTheTimeDeclare2() {
        String array[] = new String[] {
                "Toyota", "Mercedes", "BMW", "Volkswagen", "Skoda" };
    }

    //Using Arrays.fill()
    @Test
    public void arrayFill3() {
        long array[] = new long[5];
        //填入相同的值
        Arrays.fill(array, 30);
    }

    //Using Arrays.setAll()
    @Test
    public void arraySetAll4() {
        int[] array = new int[20];
        Arrays.setAll(array, p -> p > 9 ? 0 : p);
        Arrays.stream(array).forEach(i ->log.debug(i + ""));
    }
}
