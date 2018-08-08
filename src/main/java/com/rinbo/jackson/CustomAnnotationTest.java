package com.rinbo.jackson;

import com.fasterxml.jackson.annotation.JacksonAnnotationsInside;
import com.fasterxml.jackson.annotation.JsonIgnoreType;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.io.IOException;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Date;

@Slf4j
public class CustomAnnotationTest {
    @Retention(RetentionPolicy.RUNTIME)
    @JacksonAnnotationsInside
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonPropertyOrder({ "name", "id", "dateCreated" })
    public @interface CustomAnnotation {}

    @CustomAnnotation
    @AllArgsConstructor
    public class BeanWithCustomAnnotation {
        public int id;
        public String name;
        public Date dateCreated;
    }

    @Test
    //自定义注解需要加上@JacksonAnnotationsInside
    public void testCustomAnnotation()
            throws JsonProcessingException {
        BeanWithCustomAnnotation bean
                = new BeanWithCustomAnnotation(1, "My bean", null);
        String result = new ObjectMapper().writeValueAsString(bean);

        log.debug("result : {} ", result);
    }

    @AllArgsConstructor
    public class Item {
        public int id;
        public String itemName;
        public JacksonTest.User owner;

    }

    @JsonIgnoreType
    public class MyMixInForIgnoreType {}

    @Test
    public void testMixIn()
            throws JsonProcessingException {
        Item item = new Item(1, "book", null);

        String result = new ObjectMapper().writeValueAsString(item);
        log.debug("result : {} ", result);

        ObjectMapper mapper = new ObjectMapper();
        mapper.addMixIn(JacksonTest.User.class, MyMixInForIgnoreType.class);

        result = mapper.writeValueAsString(item);
        log.debug("result : {} ", result);
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonPropertyOrder({ "name", "id" })
    @AllArgsConstructor
    public class MyBean {
        public int id;
        public String name;
    }

    @Test
    public void testDisable()
            throws IOException {
        MyBean bean = new MyBean(1, null);

        ObjectMapper mapper = new ObjectMapper();
        mapper.disable(MapperFeature.USE_ANNOTATIONS);
        String result = mapper.writeValueAsString(bean);

        log.debug("result : {} ", result);
    }
}
