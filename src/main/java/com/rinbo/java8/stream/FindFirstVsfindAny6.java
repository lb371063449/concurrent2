package com.rinbo.java8.stream;


import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Slf4j
public class FindFirstVsfindAny6 {
    @Test
    public void createStream_whenFindAnyResultIsPresent_thenCorrect() {
        List<String> list = Arrays.asList("A","B","C","D");

        Optional<String> result = list.stream().findAny();
        log.debug(result.get());
    }

    @Test
    public void createParallelStream_whenFindAnyResultIsNotFirst_thenCorrect() {
        List<Integer> list = Arrays.asList(1, 2, 3, 4, 5);
        Optional<Integer> result = list
                .stream().parallel()
                .filter(num -> num < 4).findAny();
        log.debug(result.get() + "");

    }

    public List<String> getEvenIndexedStrings(String[] names) {
        List<String> evenIndexedNames = IntStream
                .range(0, names.length)
                .filter(i -> i % 2 == 0)
                .mapToObj(i -> names[i])
                .collect(Collectors.toList());

        return evenIndexedNames;
    }

    @Test
    public void whenCalled_thenReturnListOfEvenIndexedStrings() {
        String[] names = {"Afrim", "Bashkim", "Besim", "Lulzim", "Durim", "Shpetim"};
        List<String> expectedResult = Arrays.asList("Afrim", "Besim", "Durim");
        List<String> actualResult = getEvenIndexedStrings(names);
    }
}
