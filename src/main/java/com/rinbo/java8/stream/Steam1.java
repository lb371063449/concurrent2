package com.rinbo.java8.stream;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.Supplier;
import java.util.regex.Pattern;
import java.util.stream.*;

//Java 8 streams can’t be reused.
@Slf4j
public class Steam1 {

    // Streams can be created from different element sources
    // 例如 collection or array with the help of stream() and of() methods
    @Test
    public void test_create1_Arrays() {
        String[] arr = new String[]{"a", "b", "c"};
        Stream<String> stream = Arrays.stream(arr);
        stream = Stream.of(arr);
    }

    // A stream() default method is added to the Collection interface
    // and allows creating a Stream<T> using any collection as an element source:
    @Test
    public void test_create2_stream() {
        List<String> list = Arrays.asList("a", "b");
        Stream<String> stream = list.stream();
    }

    //Empty Stream
    @Test
    public void test_create3_empty() {
        Stream<String> streamEmpty = Stream.empty();
    }

    //Stream.builder()
    @Test
    public void test_create4_build() {
        Stream<String> streamBuilder = Stream.<String>builder().add("a").add("b").add("c").build();
    }

    //Stream.generate()
    @Test
    public void test_create5_generate() {
        // As the resulting stream is 无穷的, developer should specify the 期望 size
        Stream.generate(() -> "element").limit(10).forEach((e) -> log.debug(e));
    }

    //Stream.iterate()
    @Test
    public void test_create6_iterate() {
        // The first element of the resulting stream is a first parameter of the iterate() method.
        Stream.iterate(10, n -> n - 2).limit(22).forEach((e) -> log.debug(e + ""));
    }

    //Stream of Primitives
    @Test
    public void test_create7_primitives() {
        //range不包括第二个参数
        IntStream intStream = IntStream.range(1, 3);
        intStream.forEach((i) -> log.debug(i + ""));
        //range包括第二个参数
        LongStream longStream = LongStream.rangeClosed(1, 3);
        longStream.forEach((i) -> log.debug(i + ""));

        Random random = new Random();
        //3为stream的size
        DoubleStream doubleStream = random.doubles(3);
        doubleStream.forEach((i) -> log.debug(i + ""));
    }

    //Stream of String
    @Test
    public void test_create8_string() {
        IntStream streamOfChars = "abc".chars();
        streamOfChars.forEach((i) -> log.debug((char) i + ""));

        Stream<String> streamOfString = Pattern.compile(", ").splitAsStream("a, b, c");
        streamOfString.forEach((i) -> log.debug(i + ""));
    }

    //Stream of File
    @Test
    public void test_create9_file() throws IOException {
        Path path = Paths.get("D:\\work\\workspace\\foundation\\src\\main\\java\\com\\rinbo\\java8\\stream\\Steam1.java");
        Stream<String> streamOfStrings = Files.lines(path);
        Stream<String> streamWithCharset =
                Files.lines(path, Charset.forName("UTF-8"));
        streamWithCharset.forEach((i) -> System.out.println(i));
    }

    //Custom Type of Elements
    @Test
    public void test_create10_custom() throws IOException {
        Supplier<UUID> randomUUIDSupplier = UUID::randomUUID;
        Stream<UUID> infiniteStreamOfRandomUUID = Stream.generate(randomUUIDSupplier);
        List<UUID> randomInts = infiniteStreamOfRandomUUID
                .skip(10)
                .limit(10)
                .collect(Collectors.toList());
    }

    //Referencing a Stream
    @Test
    public void test_refernce() {
        List<String> elements =
                Stream.of("a", "b", "c").filter(element -> element.contains("b"))
                        .collect(Collectors.toList());
        // Java 8 streams can’t be reused
        Optional<String> anyElement = elements.stream().findAny();
        Optional<String> firstElement = elements.stream().findFirst();
    }

    //Stream Pipeline
    @Test
    public void test_pipeline() {
        //skip跳过第n个元素，n从1开始
        List<String> list = Arrays.asList("abc1", "abc2", "abc3");
        long size = list.stream().skip(1)
                .map(element -> element.substring(0, 3)).sorted().count();
        log.debug(size + "");
    }

    @Test
    public void test_operation1_iterate() {
        List<String> list = Arrays.asList("a", "b");
        boolean isExist = list.stream()
                .anyMatch(element -> element.contains("a"));
    }

    @Test
    public void test_operation2_filter() {
        ArrayList<String> list = new ArrayList<>();
        list.add("One");
        list.add("OneAndOnly");
        list.add("Derek");
        list.add("Change");
        list.add("factory");
        list.add("justBefore");
        list.add("Italy");
        list.add("Italy");
        list.add("Thursday");
        list.add("");
        Stream<String> stream = list.stream()
                .filter(element -> element.contains("d"));
    }

    @Test
    public void test_operation3_map1() {
        List<String> uris = new ArrayList<>();
        uris.add("C:\\My.txt");
        Stream<Path> stream = uris.stream()
                .map(uri -> Paths.get(uri));
    }

    @Test
    public void test_operation3_map2() {
        List<String> list = Arrays.asList("a", "b", "h");
        boolean isValid = list.stream().anyMatch(element -> element.contains("h")); // true
        boolean isValidOne = list.stream().allMatch(element -> element.contains("h")); // false
        boolean isValidTwo = list.stream().noneMatch(element -> element.contains("h")); // false
    }

    @Test
    public void test_operation4_reduce1() {
        List<Integer> integers = Arrays.asList(1, 1, 1);
        Integer reduced = integers.stream()
                .reduce(23, (a, b) -> a + b);
    }

    @Test
    public void test_operation4_reduce2() {
        //指定了reduce的初始值10
        int reducedTwoParams =
                IntStream.range(1, 4).reduce(10, (a, b) -> a + b);
        log.debug(reducedTwoParams + "");
    }

    @Test
    public void test_operation4_reduce3() {
        //三个参数的reduce必须是parallelStream()
        //第三个参数操作的是第二个参数的集
        int reducedParallel = Arrays.asList(1, 2, 3).parallelStream()
                .reduce(10, (a, b) -> a + b, (a, b) -> {
                    log.info("combiner was called");
                    return a + b;
                });
        log.debug(reducedParallel + "");
    }

    @Test
    public void test_operation5_collect1() {
        List<String> list = Arrays.asList("a", "b");
        List<String> resultList
                = list.stream().map(element -> element.toUpperCase()).collect(Collectors.toList());
    }

    @Test
    public void test_operation5_collect2() {
        //Reduction of a stream can also be executed by another terminal operation
        List<Product> productList = Arrays.asList(new Product(23, "potatoes"),
                new Product(14, "orange"), new Product(13, "lemon"),
                new Product(23, "bread"), new Product(13, "sugar"));
        //1、Converting a stream to the Collection (Collection, List or Set):
        List<String> collectorCollection =
                productList.stream().map(Product::getName).collect(Collectors.toList());
        //2、Reducing to String
        String listToString = productList.stream().map(Product::getName)
                .collect(Collectors.joining(", ", "[", "]"));
        log.debug(listToString);

        //3、Processing the average value of all numeric elements of the stream
        double averagePrice = productList.stream()
                .collect(Collectors.averagingInt(Product::getPrice));
        log.debug(averagePrice + "");

        //4、Processing the sum of all numeric elements of the stream
        int summingPrice = productList.stream()
                .collect(Collectors.summingInt(Product::getPrice));
        log.debug(summingPrice + "");
        //5、Collecting statistical information about stream’s elements
        //统计信息
        IntSummaryStatistics statistics = productList.stream()
                .collect(Collectors.summarizingInt(Product::getPrice));
        log.debug(statistics.toString());

        //6、Grouping of stream’s elements according to the specified function
        Map<Integer, List<Product>> collectorMapOfLists = productList.stream()
                .collect(Collectors.groupingBy(Product::getPrice));

        //7、Dividing stream’s elements into groups according to some predicate
        Map<Boolean, List<Product>> mapPartioned = productList.stream()
                .collect(Collectors.partitioningBy(element -> element.getPrice() > 15));

        //8、Pushing the collector to perform additional transformation
        //he collector has converted a stream to a Set and then created the unmodifiable Set out of it
        Set<Product> unmodifiableSet = productList.stream()
                .collect(Collectors.collectingAndThen(Collectors.toSet(),
                        Collections::unmodifiableSet));
        //9、Custom collector
        // use the method of() of the type Collector
        Collector<Product, ?, LinkedList<Product>> toLinkedList =
                Collector.of(LinkedList::new, LinkedList::add,
                        (first, second) -> {
                            first.addAll(second);
                            return first;
                        });
        LinkedList<Product> linkedListOfPersons =
                productList.stream().collect(toLinkedList);
    }

    //Lazy Invocation
    @Test
    public void test_lazyinvocation() {
        //filter被调用2此，map只调用一次
        List<String> list = Arrays.asList("abc1", "abc2", "abc3");
        Optional<String> stream = list.stream().filter(element -> {
            log.info("filter() was called");
            return element.contains("2");
        }).map(element -> {
            log.info("map() was called");
            return element.toUpperCase();
        }).findFirst();
    }

    //Order of Execution
    @Test
    public void test_orderOfExecution() {
        List<String> list = Arrays.asList("abc1", "abc2", "abc3");
        // intermediate operations which reduce the size of the stream should be placed
        // before operations which are applying to each element.
        long size = list.stream().skip(2).map(element -> {
            return element.substring(0, 3);
        }).count();
    }

    @Data
    @AllArgsConstructor
    private class Product {
        private int price;
        private String name;
    }
}
