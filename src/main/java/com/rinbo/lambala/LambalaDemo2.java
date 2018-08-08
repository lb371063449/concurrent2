package com.rinbo.lambala;

import org.junit.Test;

@FunctionalInterface
interface DefaultInterface {
    double calculate(int a);

    //默认函数
    default double sqrt(int a) {
        return Math.sqrt(a);
    }
}

@FunctionalInterface
interface Converter<F, T> {
    T conver(F form);
}

class Something {
    String startsWith(String s) {
        return String.valueOf(s.charAt(0));
    }
}

class Person {
    String firstName;
    String lastName;

    Person() {
    }

    Person(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
    }
}

interface PersonFactory<P extends Person> {
    P create(String firstName, String lastName);
}


public class LambalaDemo2 {
    @Test
    public void test1() {
        DefaultInterface d = new DefaultInterface() {
            @Override
            public double calculate(int a) {
                return a * 10;
            }
        };
        System.out.println(d.calculate(120));
        //调用接口默认函数
        System.out.println(d.sqrt(16));

        DefaultInterface d2 = a -> a * a;
        System.out.println(d2.calculate(120));

        //Java 8 允许你通过::关键字获取方法或者构造函数的的引用
        //::相当于return+函数调用
        Converter<String, Integer> c1 = Integer::valueOf;
        //等价于
        Converter<String, Integer> c2 = (i)->{return Integer.valueOf(i);};

        Something something = new Something();
        Converter<String, String> converter2 = something::startsWith;
        //等价于
        Converter<String, String> converter22 = (i)->{return something.startsWith(i);};
        System.out.println(converter2.conver("xx"));

        //通过Person::new来创建一个Person类构造函数的引用。
        PersonFactory<Person> personFactory = Person::new;
        //等价于
        PersonFactory<Person> personFactory2 = (fname,lname)->{return new Person(fname,lname);};
        Person person = personFactory.create("Peter", "Parker");
    }

    static int outerStaticNum;
    int outerNum;
    @Test
    public void test2() {
        Converter<Integer, String> stringConverter1 = (from) -> {
            //与在lambda表达式的内部能获取到成员变量
            outerNum = 23;
            return String.valueOf(from);
        };

        Converter<Integer, String> stringConverter2 = (from) -> {
            //在lambda表达式的内部能获取到静态变量
            outerStaticNum = 72;
            return String.valueOf(from);
        };
        //可以访问lambda表达式外部的final局部变量
        final int a = 10;
        //num在编译的时候被隐式地当做final变量来处理
        int num = 1;
        //Lambda
        Converter<String, Integer> converter = form -> Integer.parseInt(form + num);
        System.out.println(converter.conver("333"));
    }
}

