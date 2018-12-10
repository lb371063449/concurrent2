package com.rinbo.collections;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

public class ConcurrentModificationExceptionTest {

    private List<Integer> newArray() {
        List<Integer> integers = new ArrayList<>();
        integers.add(1);
        integers.add(2);
        integers.add(3);
        return integers;
    }

    //触发ConcurrentModificationException
    @Test
    public void shouldThrowException() throws InterruptedException {

        List<Integer> integers = newArray();

        for (Integer integer : integers) {
            integers.remove(1);
        }
    }

    //1、Using an Iterator Directly
    @Test
    public void usingAnIteratorDirectly() throws InterruptedException {

        List<Integer> integers = newArray();

        for (Iterator<Integer> iterator = integers.iterator(); iterator.hasNext(); ) {
            Integer integer = iterator.next();
            if (integer == 2) {
                iterator.remove();
            }
        }
    }

    //2、Not Removing During Iteration
    @Test
    public void notRemovingDuringIteration() throws InterruptedException {
        List<Integer> integers = newArray();
        List<Integer> toRemove = new ArrayList<>();
        for (Integer integer : integers) {
            if (integer == 2) {
                toRemove.add(integer);
            }
        }
        integers.removeAll(toRemove);
        System.out.println(integers.size());
    }

    //3、Using removeIf()
    @Test
    public void usingRemoveIf() throws InterruptedException {
        List<Integer> integers = newArray();
        integers.removeIf(i -> i == 2);
        System.out.println(integers.size());
    }

    //4、Filtering Using Streams
    @Test
    public void usingStream() throws InterruptedException {
        List<Integer> integers = newArray();
        List<Integer> collect = integers.stream().filter(i -> i != 2).collect(Collectors.toList());
        collect.stream().forEach(i -> System.out.println(i));
    }
}