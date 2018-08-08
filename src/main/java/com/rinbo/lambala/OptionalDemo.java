package com.rinbo.lambala;

import lombok.Data;
import org.junit.Test;

import java.util.Optional;

public class OptionalDemo {

    @Test
    public void test() {
        //of-为非null的值创建一个Optional
        Optional<String> name = Optional.of("Rinbo");
        System.out.println(name.get());

        //传入参数为null，抛出NullPointerException.
        Optional<String> someNull = Optional.of(null);

        //ofNullable-为指定的值创建一个Optional，如果指定的值为null，则返回一个空的Optional
        Optional<String> empty = Optional.ofNullable(null);

        //isPresent-如果值存在返回true，否则返回false。
        System.out.println(empty.isPresent());

        //get-如果Optional有值则将其返回，否则抛出NoSuchElementException
        System.out.println(name.get());

        //ifPresent-如果Optional实例有值则为其调用consumer，否则不做处理
        name.ifPresent(s -> System.out.println("this value is : " + s));

        //orElse-如果值不为null，orElse方法返回Optional实例的值。如果为null，返回传入的消息。
        System.out.println(empty.orElse("not empty"));

        //orElseGet-与orElse方法类似，区别在于orElse传入的是默认值,orElseGet可以接受一个lambda表达式生成默认值
        System.out.println(empty.orElseGet(() -> "this is empty"));
        System.out.println(name.orElseGet(() -> "this is not empty"));
        try {
            //orElseThrow-orElseThrow与orElse方法类似。与返回默认值不同，orElseThrow会抛出lambda表达式或方法生成的异常
            empty.orElseThrow(ValueAbsentException::new);
        } catch (Throwable e) {
            e.printStackTrace();
        }

        //map-如果有值，则对其执行调用mapping函数得到返回值。如果返回值不为null，则创建包含mapping返回值的Optional作为map方法返回值，否则返回空Optional
        System.out.println(name.map(s -> s.toUpperCase()).orElse("null"));

        //flatMap-flatMap与map（Function）非常类似,map方法中的lambda表达式返回值可以是任意类型，在map函数返回之前会包装为Optional。
        //但flatMap方法中的lambda表达式返回值必须是Optionl实例。
        System.out.println(name.flatMap(s -> Optional.of(s.toUpperCase())).orElse("null"));

        //filter-方法检查给定的Option值是否满足某些条件。如果满足则返回同一个Option实例，否则返回空Optional。
        System.out.println(name.filter(s -> s.length() > 6).orElse("the length is less than 6"));
    }

    public static String getName(User u) {
        return Optional.ofNullable(u)
                .map(user->user.name)
                .orElse("Unknown");
        //相当于下面代码
//        if (u == null)
//            return "Unknown";
//        return u.name;
    }

//    public static string getChampionName(Competition comp) throws IllegalArgumentException {
//        return Optional.ofNullable(comp)
//                .map(c->c.getResult())
//                .map(r->r.getChampion())
//                .map(u->u.getName())
//                .orElseThrow(()->new IllegalArgumentException("The value of param comp isn't available."));
        //相当于下面代码
//        if (comp != null) {
//            CompResult result = comp.getResult();
//            if (result != null) {
//                User champion = result.getChampion();
//                if (champion != null) {
//                    return champion.getName();
//                }
//            }
//        }
//        throw new IllegalArgumentException("The value of param comp isn't available.");
//    }

    @Data
    private class User {
        private String name;
        private int id;
    }
}


class ValueAbsentException extends Throwable {
    public ValueAbsentException() {
        super();
    }

    public ValueAbsentException(String msg) {
        super(msg);
    }

    @Override
    public String getMessage() {
        return "No value present in the Optional instance";
    }
}
