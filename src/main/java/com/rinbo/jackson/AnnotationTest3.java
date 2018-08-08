package com.rinbo.jackson;

import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class AnnotationTest3 {
    @AllArgsConstructor
    static class Zoo {
        public Animal animal;

        @JsonTypeInfo(
                use = JsonTypeInfo.Id.NAME,
                property = "type")
        @JsonSubTypes({
                @JsonSubTypes.Type(value = Dog.class, name = "dog"),
                @JsonSubTypes.Type(value = Cat.class, name = "cat")
        })
        @AllArgsConstructor
        public static class Animal {
            public String name;
        }

        @JsonTypeName("dog")
        public static class Dog extends Animal {
            public double barkVolume;

            public Dog(String name) {
                super(name);
            }
        }

        @JsonTypeName("cat")
        public static class Cat extends Animal {
            boolean likesCream;
            public int lives;

            public Cat(String name) {
                super(name);
            }
        }
    }

    @Test
    public void testDuoTai()
            throws JsonProcessingException {
        Zoo.Dog dog = new Zoo.Dog("lacy");
        Zoo zoo = new Zoo(dog);

        String result = new ObjectMapper().writeValueAsString(zoo);
        log.debug("result : {} ", result);

    }

    @AllArgsConstructor
    @ToString
    static class UnwrappedUser {
        public int id;

        @JsonUnwrapped
        public Name name;

        @AllArgsConstructor
        @ToString
        public static class Name {
            public String firstName;
            public String lastName;
        }
    }

    @Test
    //@JsonUnwrapped将内部类中的属性提出来
    public void testJsonUnwrapped()
            throws JsonProcessingException, ParseException {
        UnwrappedUser.Name name = new UnwrappedUser.Name("John", "Doe");
        UnwrappedUser user = new UnwrappedUser(1, name);

        String result = new ObjectMapper().writeValueAsString(user);
        log.debug("result : {} ", result);
    }

    static class Views {
        public static class Public {
        }

        public static class Internal extends Public {
        }
    }

    @AllArgsConstructor
    class Item {
        @JsonView(Views.Public.class)
        public int id;

        @JsonView(Views.Public.class)
        public String itemName;

        @JsonView(Views.Internal.class)
        public String ownerName;
    }

    @Test
    public void whenSerializingUsingJsonView_thenCorrect()
            throws JsonProcessingException {
        Item item = new Item(2, "book", "John");

        String result = new ObjectMapper()
                //设置那些View 类可以序列化
                .writerWithView(Views.Public.class)
                .writeValueAsString(item);
        log.debug("result : {} ", result);
    }

    @AllArgsConstructor
    class ItemWithRef {
        public int id;
        public String itemName;

        @JsonManagedReference
        public UserWithRef owner;
    }

    class UserWithRef {
        public int id;
        public String name;

        public UserWithRef(int id, String name) {
            this.id = id;
            this.name = name;
        }

        @JsonBackReference
        public List<ItemWithRef> userItems;

        public void addItem(ItemWithRef item) {
            if (userItems == null) {
                userItems = new ArrayList<>();
            }
            userItems.add(item);
        }
    }

    @Test
    public void testJsonBackReference()
            throws JsonProcessingException {
        UserWithRef user = new UserWithRef(1, "John");
        ItemWithRef item = new ItemWithRef(2, "book", user);
        user.addItem(item);

        String result = new ObjectMapper().writeValueAsString(item);
        String result2 = new ObjectMapper().writeValueAsString(user);
        log.debug("result : {} ", result);
        log.debug("result : {} ", result2);
    }

    @JsonIdentityInfo(
            generator = ObjectIdGenerators.PropertyGenerator.class,
            property = "id")
    @AllArgsConstructor
    public class ItemWithIdentity {
        public int id;
        public String itemName;
        public UserWithIdentity owner;
    }

    @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class,
            property = "id")
    public class UserWithIdentity {
        public int id;
        public String name;
        public List<ItemWithIdentity> userItems;

        public UserWithIdentity(int id, String name) {
            this.id = id;
            this.name = name;
        }

        public void addItem(ItemWithIdentity item) {
            if (userItems == null) {
                userItems = new ArrayList<>();
            }
            userItems.add(item);
        }
    }

    @Test
    public void testJsonIdentityInfo()
            throws JsonProcessingException {
        UserWithIdentity user = new UserWithIdentity(1, "John");
        ItemWithIdentity item = new ItemWithIdentity(2, "book", user);
        user.addItem(item);
        String result = new ObjectMapper().writeValueAsString(user);

        log.debug("result : {} ", result);
    }

    @JsonFilter("myFilter")
    @AllArgsConstructor
    public class BeanWithFilter {
        public int id;
        public String name;
    }
    @Test
    public void whenSerializingUsingJsonFilter_thenCorrect()
            throws JsonProcessingException {
        BeanWithFilter bean = new BeanWithFilter(1, "My bean");
        //指定filter
        FilterProvider filters = new SimpleFilterProvider().addFilter("myFilter",
                SimpleBeanPropertyFilter.filterOutAllExcept("id"));

        String result = new ObjectMapper().writer(filters).writeValueAsString(bean);

        log.debug("result : {} ", result);
    }
}
