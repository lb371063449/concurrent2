package com.rinbo.jackson;

import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.InjectableValues;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class AnnotationTest2 {

    @Data
    static class BeanWithCreator {
        public int id;
        public String name;

        @JsonCreator
        public BeanWithCreator(
                @JsonProperty("id") int id,
                @JsonProperty("theName") String name) {
            this.id = id;
            this.name = name;
        }
    }

    @Test
    //@JsonCreator用于处理json的key与对象属性名称不匹配
    public void testJsonCreator() throws IOException {
        String json = "{\"id\":1,\"theName\":\"My bean\"}";
        BeanWithCreator result = new ObjectMapper().readerFor(BeanWithCreator.class).readValue(json);
        log.debug("result : {}", result);
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    static class BeanWithInject {
        @JacksonInject
        public int id;

        public String name;
    }

    @Test
    //@JacksonInject标注的值从外面提供
    public void testJacksonInject()
            throws IOException {

        String json = "{\"name\":\"My bean\"}";

        InjectableValues inject = new InjectableValues.Std()
                .addValue(int.class, 1);
        BeanWithInject result = new ObjectMapper().reader(inject)
                .forType(BeanWithInject.class)
                .readValue(json);
        log.debug("result : {}", result);
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    static class ExtendableBean {
        public String name;
        private Map<String, String> properties;

        @JsonAnySetter
        public void add(String key, String value) {
            if (properties == null) {
                properties = new HashMap<>();
            }
            properties.put(key, value);
        }
    }

    @Test
    public void testJsonAnySetter()
            throws IOException {
        String json = "{\"name\":\"My bean\",\"attr2\":\"val2\",\"attr1\":\"val1\"}";
        ExtendableBean result = new ObjectMapper()
                .readerFor(ExtendableBean.class)
                .readValue(json);
        log.debug("result : {}", result);
    }

    @ToString
    static class MyBean {
        public int id;
        private String name;

        @JsonSetter("name")
        public void setTheName(String name) {
            this.name = name + "-----------";
        }
    }

    @Test
    public void testJsonSetter()
            throws IOException {

        String json = "{\"id\":1,\"name\":\"My bean\"}";
        MyBean bean = new ObjectMapper()
                .readerFor(MyBean.class)
                .readValue(json);
        log.debug("result : {}", bean);
    }

    static public class CustomDateDeserializer extends StdDeserializer<Date> {

        private static SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss");

        public CustomDateDeserializer() {
            this(null);
        }

        public CustomDateDeserializer(Class<?> vc) {
            super(vc);
        }

        @Override
        public Date deserialize(JsonParser jsonparser, DeserializationContext context) throws IOException {
            String date = jsonparser.getText();
            try {
                return formatter.parse(date);
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @ToString
    static class Event {
        public String name;
        @JsonDeserialize(using = CustomDateDeserializer.class)
        public Date eventDate;
    }

    @Test
    public void testJsonDeserialize()
            throws IOException {

        String json = "{\"name\":\"party\",\"eventDate\":\"20-12-2014 02:30:00\"}";

        SimpleDateFormat df
                = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss");
        Event event = new ObjectMapper()
                .readerFor(Event.class)
                .readValue(json);
        log.debug("result : {}", event);
    }

    @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
    @AllArgsConstructor
    @ToString
    static public class PrivateBean {
        private int id;
        private String name;
    }

    @Test
    //@JsonAutoDetect可以序列化private字段，而不需要提供getter/setter
    public void testJsonAutoDetect() throws JsonProcessingException {
        PrivateBean bean = new PrivateBean(1, "My bean");
        String result = new ObjectMapper()
                .writeValueAsString(bean);
        log.debug("result : {}", result);

    }
}
