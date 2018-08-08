package com.rinbo.lambala;

import org.junit.Test;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class LambalaDemo {
    @Test
    public void testPredicate() {
        //断言型接口-Predicate
        Predicate<String> predicate = s -> s.length() > 0;
        System.out.println(predicate.test("foo"));
        System.out.println(predicate.negate().test("foo"));
        Predicate<Boolean> isNull = Objects::isNull;
        Predicate<Boolean> nonNull = Objects::nonNull;
        Predicate<String> isEmpty = String::isEmpty;
        Predicate<String> nonEmpty = isEmpty.negate();
    }

    @Test
    public void testPredicateFunction() {
        //函数型接口-Function，接收一个参数，并返回单一的结果
        Function<String, Integer> function = Integer::valueOf;
        System.out.println(function.apply("22"));
    }

    @Test
    public void testSupplier() {
        //供给型接口-Supplier，产生一个给定类型的结果
        Supplier<Person> supplier = () -> new Person();
        Supplier<Person> supplier1 = Person::new;
        System.out.println(supplier.get());
        System.out.println(supplier1.get());

    }

    @Test
    public void testConsumer() {
        //消费型接口-Consumer，代表了在一个输入参数上需要进行的操作
        Consumer<Person> consumer = p -> {
            System.out.println("Hello, " + p.firstName);
        };
        consumer.accept(new Person("a", "b"));
    }

    @Test
    public void testStream() {
        //java.util.Stream表示了某一种元素的序列，在这些元素上可以进行各种操作
        List<String> stringCollection = new ArrayList<>();
        stringCollection.add("ddd2");
        stringCollection.add("aaa2");
        stringCollection.add("bbb1");
        stringCollection.add("aaa1");
        stringCollection.add("bbb3");
        stringCollection.add("ccc");
        stringCollection.add("bbb2");
        stringCollection.add("ddd1");

        //Filter接受一个predicate接口类型的变量，并将所有流对象中的元素进行过滤。该操作是一个中间操作
        stringCollection.stream().filter(s -> {
            return s.startsWith("a");
        }).forEach(s -> {
            System.out.println(s);
        });

        //Sorted是一个中间操作，能够返回一个排过序的流对象的视图。sorted只是创建一个流对象排序的视图，而不会改变原来集合中元素的顺序
        stringCollection.stream().sorted().filter(s -> {
            return s.startsWith("a");
        }).forEach(System.out::println);

        //map是一个对于流对象的中间操作，通过给定的方法，它能够把流对象中的每一个元素对应到另外一个对象上
        stringCollection.stream().map(s -> {
            return s.toUpperCase();
        }).forEach(System.out::println);

        //匹配操作有多种不同的类型，都是用来判断某一种规则是否与流对象相互吻合的。
        //所有的匹配操作都是终结操作，只返回一个boolean类型的结果
        boolean anyMatch = stringCollection.stream().anyMatch(s -> s.startsWith("a"));
        System.out.println(anyMatch);

        boolean allMatch = stringCollection.stream().allMatch(s -> s.startsWith("a"));
        System.out.println(allMatch);

        boolean noneMatch = stringCollection.stream().noneMatch(s -> s.startsWith("a"));
        System.out.println(noneMatch);

        //Count是一个终结操作，它的作用是返回一个数值
        long count = stringCollection.stream().count();
        System.out.println(count);

        //reduce该操作是一个终结操作，它能够通过某一个方法对元素进行削减操作
        Optional<String> reduce = stringCollection.stream().reduce((s, s2) -> s + "#" + s2);
        reduce.ifPresent(System.out::println);

        int max = 1000000;
        List<String> values = new ArrayList<>(max);
        for (int i = 0; i < max; i++) {
            UUID uuid = UUID.randomUUID();
            values.add(uuid.toString());
        }

        //并行Streams
        long count1 = values.parallelStream().count();
        System.out.println(count1);
    }
}
