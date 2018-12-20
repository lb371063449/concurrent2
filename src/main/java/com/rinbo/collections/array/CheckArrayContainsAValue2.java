package com.rinbo.collections.array;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

@Slf4j
public class CheckArrayContainsAValue2 {

    String[] seedArray(int length) {
        String[] strings = new String[length];
        Random value = new Random();
        for (int i = 0; i < length; i++) {
            strings[i] = String.valueOf(value.nextInt());
        }
        return strings;
    }

    @Test
    public void searchList() {
        boolean result = Arrays.asList(seedArray(10)).contains("21");
        System.out.println(result);
    }

    // Searching a hash table, the structure underlying a HashSet, has a time complexity of 0(1),
    // while an array, which underlies the ArrayList is 0(n).
    @Test
    public void searchArrayReuseSet() {
        Set asSet = new HashSet<>(Arrays.asList(seedArray(10)));
        for (int i = 0; i < 10; i++) {
            asSet.contains("T");
        }
    }

    @Test
    public void binarySearch() {
        String[] array = seedArray(100);
        //必须排序
        Arrays.sort(array);
        int t = Arrays.binarySearch(array, "T");
        log.debug(t + "");
    }
}
