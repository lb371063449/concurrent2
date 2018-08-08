package com.rinbo.jackson;

import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.Date;

@Slf4j
public class JacksonTest {

    @Data
    @JsonInclude(JsonInclude.Include.NON_NULL)
    //可以再类上+注解
//    @JsonIgnoreProperties(value = { "intValue" })

    class MyDto {
        @JsonProperty("name")
        private String stringValue;
        //字段上+注解
        @JsonIgnore
        private int intValue;

        private boolean booleanValue;

        private Sub sub;
    }

    @Data
    @AllArgsConstructor
    //忽略整个类型
    @JsonIgnoreType
    class Sub {
        private String name;
    }

    @Test
    public void testChangeFile() throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        //全局设置不处理非空字段
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        MyDto dtoObject = new MyDto();
//        dtoObject.setStringValue("a");
        dtoObject.setSub(new Sub("a"));
        dtoObject.setIntValue(33);

        String dtoAsString = mapper.writeValueAsString(dtoObject);
        log.debug("result : {}",dtoAsString);
    }

    ObjectMapper mapper = new ObjectMapper();

    @Data
    @AllArgsConstructor
    static class User {
        private String firstName;
        private String lastName;
        //@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd@HH:mm:ss.SSSZ")
        //@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
        //指定locale
        //@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd@HH:mm:ss.SSSZ", locale = "en_GB")
        //指定shape
        @JsonFormat(shape = JsonFormat.Shape.NUMBER)
        private Date createdDate = new Date();
    }
    @Test
    public void TestJsonFormat() throws JsonProcessingException {
        String result = mapper.writeValueAsString(new User("aa", "bb", new Date()));
        log.debug("result : {}",result);
    }
}
