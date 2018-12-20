package com.rinbo.collections.array;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.Arrays;

@Slf4j
public class HowToCopyAnArray1 {


    @Test
    public void theSystemClass1() {
        int[] array = {23, 43, 55, 12, 65, 88, 92};
        int[] copiedArray = new int[3];

        System.arraycopy(array, 2, copiedArray, 0, 3);
        Arrays.stream(copiedArray).forEach(i->log.debug(i + " "));
    }

    //Arrays是浅拷贝
    @Test
    public void theArraysCoypOf2_1() {
        int[] array = {23, 43, 55, 12};
        int newLength = array.length;
        int[] copiedArray = Arrays.copyOf(array, newLength);
        Arrays.stream(copiedArray).forEach(i->log.debug(i + " "));
    }

    @Test
    public void theArraysCoypOf2_2() {
        int[] array = {23, 43, 55, 12};
        int newLength = array.length;
        int[] copiedArray = Arrays.copyOfRange(array, 2,4);
        Arrays.stream(copiedArray).forEach(i->log.debug(i + " "));
    }

    //浅拷贝
    @Test
    public void objectClone3() {
        int[] array = {23, 43, 55, 12};
        int[] copiedArray = array.clone();
    }

    //浅拷贝
    @Test
    public void java8stream() {
        String[] strArray = {"orange", "red", "green'"};
        String[] copiedArray = Arrays.stream(strArray).toArray(String[]::new);
    }
}


