package com.rinbo.java8.stream;

import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Slf4j
public class Comparison7 {

    @Data
    @AllArgsConstructor
    public class Human {
        private String name;
        private int age;
    }

    //1、使用匿名内部类
    @Test
    public void givenPreLambda_whenSortingEntitiesByName_thenCorrectlySorted() {
        List<Human> humans = Lists.newArrayList(
                new Human("Sarah", 10),
                new Human("Jack", 12)
        );

        Collections.sort(humans, new Comparator<Human>() {
            @Override
            public int compare(Human h1, Human h2) {
                return h1.getName().compareTo(h2.getName());
            }
        });
        log.debug("the first hunam is : {}", humans.get(0));
    }

    //2、使用Lambda Support
    @Test
    public void whenSortingEntitiesByName_thenCorrectlySorted() {
        List<Human> humans = Lists.newArrayList(
                new Human("Sarah", 10),
                new Human("Jack", 12)
        );

        Collections.sort(humans, (h1, h2) -> h1.getName().compareTo(h2.getName()));
        log.debug("the first hunam is : {}", humans.get(0));
    }

    public static int compareByNameThenAge(Human lhs, Human rhs) {
        if (lhs.name.equals(rhs.name)) {
            return lhs.age - rhs.age;
        } else {
            return lhs.name.compareTo(rhs.name);
        }
    }

    //3、使用静态方法
    @Test
    public void
    givenMethodDefinition_whenSortingEntitiesByNameThenAge_thenCorrectlySorted() {

        List<Human> humans = Lists.newArrayList(
                new Human("Sarah", 10),
                new Human("Jack", 12)
        );

        Collections.sort(humans, Comparison7::compareByNameThenAge);
        log.debug("the first hunam is : {}", humans.get(0));
    }

    //4、省略比较逻辑
    @Test
    public void
    givenInstanceMethod_whenSortingEntitiesByNameThenAge_thenCorrectlySorted() {

        List<Human> humans = Lists.newArrayList(
                new Human("Sarah", 10),
                new Human("Jack", 12)
        );
        // We can also avoid defining even the comparison logic itself by using an instance method reference and the Comparator.
        // comparing method – which extracts(提取) and creates a Comparable based on that function.
        Collections.sort(humans, Comparator.comparing(Human::getName));
        log.debug("the first hunam is : {}", humans.get(0));
    }

    //5、反转
    @Test
    public void whenSortingEntitiesByNameReversed_thenCorrectlySorted() {
        List<Human> humans = Lists.newArrayList(
                new Human("Sarah", 10),
                new Human("Jack", 12)
        );

        Comparator<Human> comparator = (h1, h2) -> h1.getName().compareTo(h2.getName());
        //调用comparator.reversed()进行反转
        humans.sort(comparator.reversed());
        log.debug("the first hunam is : {}", humans.get(0));
    }

    //6、多个排序条件组合
    @Test
    public void
    givenComposition_whenSortingEntitiesByNameThenAge_thenCorrectlySorted() {

        List<Human> humans = Lists.newArrayList(
                new Human("Sarah", 12),
                new Human("Sarah", 10),
                new Human("Zack", 12)
        );
        //使用thenComparing来组合多个排序条件
        humans.sort(Comparator.comparing(Human::getName).thenComparing(Human::getAge));
        log.debug("the first hunam is : {}", humans.get(0));
    }
}
