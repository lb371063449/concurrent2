package com.rinbo.jackson;

import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class AnnotationTest {

    static class ExtendableBean {
        public String name;
        private Map<String, String> properties;

        public ExtendableBean(String name) {
            this.name = name;
        }

        public void add(String key, String value) {
            if (properties == null) {
                properties = new HashMap<>();
            }
            properties.put(key, value);
        }

        @JsonAnyGetter
        public Map<String, String> getProperties() {
            return properties;
        }
    }

    //@JsonAnyGetter annotation allows the flexibility of using a Map field as standard properties.
    @Test
    public void testJsonAnyGetter() throws JsonProcessingException {
        ExtendableBean bean = new ExtendableBean("My bean");
        bean.add("attr1", "val1");
        bean.add("attr2", "val2");
        String result = new ObjectMapper().writeValueAsString(bean);
        log.debug("result : {}", result);
    }

    @AllArgsConstructor
    @JsonPropertyOrder({"name", "id"})
    static class MyBean {
        public int id;
        private String name;

        @JsonGetter("name")
        public String getTheName() {
            return name + "-xxx";
        }
    }

    @Test
    //@JsonGetter annotation is an alternative to the @JsonProperty annotation to mark the specified method as a getter method
    public void testJsonGetter()
            throws JsonProcessingException {
        MyBean bean = new MyBean(1, "My bean");
        String result = new ObjectMapper().writeValueAsString(bean);
        log.debug("result : {}", result);
    }

    @Test
    public void testJsonPropertyOrder() throws JsonProcessingException {
        MyBean bean = new MyBean(1, "My bean");
        String result = new ObjectMapper().writeValueAsString(bean);
        log.debug("result : {}", result);
    }

    @Data
    @AllArgsConstructor
    static class RawBean {
        public String name;
        @JsonRawValue
        public String json;
    }

    @Test
    //@JsonRawValue中序列化的json值为设置的值
    public void testJsonRawValue() throws JsonProcessingException {
        RawBean bean = new RawBean("My bean", "{\"attr\":false}");
        String result = new ObjectMapper().writeValueAsString(bean);
        log.debug("result : {}", result);
    }

    @AllArgsConstructor
    static enum TypeEnumWithValue {
        TYPE1(1, "Type A"), TYPE2(2, "Type 2");
        private Integer id;
        private String name;

        public String getName() {
            return name;
        }

        @JsonValue
        public String aa() {
            return name + "  " + id;
        }
    }

    @Test
    //@JsonValue用于指定对象序列化时调用的方法
    public void testJsonValue() throws JsonProcessingException {
        String enumAsString = new ObjectMapper()
                .writeValueAsString(TypeEnumWithValue.TYPE1);
        log.debug("result : {}", enumAsString);
    }

    @JsonRootName(value = "user")
    @AllArgsConstructor
    static class UserWithRoot {
        public int id;
        public String name;
    }

    @Test
    public void testJsonRootName() throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.enable(SerializationFeature.WRAP_ROOT_VALUE);
        String result = mapper.writeValueAsString(new UserWithRoot(1, "aa"));
        log.debug("result : {}", result);
    }

    static class CustomDateSerializer extends StdSerializer<Date> {

        private static SimpleDateFormat formatter
                = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss");

        public CustomDateSerializer() {
            this(null);
        }

        public CustomDateSerializer(Class<Date> t) {
            super(t);
        }

        @Override
        public void serialize(
                Date value, JsonGenerator gen, SerializerProvider arg2)
                throws IOException, JsonProcessingException {
            gen.writeString(formatter.format(value));
        }
    }

    @AllArgsConstructor
    class Event {
        public String name;

        @JsonSerialize(using = CustomDateSerializer.class)
        public Date eventDate;
    }

    @Test
    public void testJsonSerialize() throws JsonProcessingException, ParseException {
        Event event = new Event("party", new Date());
        String result = new ObjectMapper().writeValueAsString(event);
        log.debug("result : {}", result);
    }
}
