package com.rinbo.jackson;

import com.fasterxml.jackson.core.*;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

@Slf4j
public class StreamTest {

    JsonGenerator jsonGenerator;
    ByteArrayOutputStream outputStream;
    String json = "{\"name\":\"Tom\",\"age\":25,\"address\":[\"Poland\",\"5th avenue\"]}";
    JsonParser parser;

    @Before
    public void before() throws IOException {
        outputStream = new ByteArrayOutputStream();
        //创建JsonFactory对象
        JsonFactory factory = new JsonFactory();
        //根据factory创建JsonGenerator对象
        jsonGenerator = factory.createGenerator(outputStream, JsonEncoding.UTF8);
        //根据factory创建JsonParser对象
        parser = factory.createParser(json);
    }

    @Test
    public void wrieteJson() throws IOException {
        //使用JsonGenerator对象写数据到流中
        jsonGenerator.writeStartObject();
        jsonGenerator.writeStringField("name", "Tom");
        jsonGenerator.writeNumberField("age", 25);
        jsonGenerator.writeFieldName("address");
        //开始写数组
        jsonGenerator.writeStartArray();
        jsonGenerator.writeString("Poland");
        jsonGenerator.writeString("5th avenue");
        jsonGenerator.writeEndArray();
        jsonGenerator.writeEndObject();
        //关闭jsonGenerator
        jsonGenerator.close();

        String json = new String(outputStream.toByteArray(), "UTF-8");
        log.debug("result : {}", json);
    }

    @Test
    public void parserJson() throws IOException {
        String parsedName = null;
        Integer parsedAge = null;
        List<String> addresses = new LinkedList<>();
        //使用JsonParser操作json字符串
        while (parser.nextToken() != JsonToken.END_OBJECT) {
            String fileName = parser.currentName();
            if ("name".equals(fileName)) {
                parser.nextToken();
                parsedName = parser.getText();
            }
            if ("age".equals(fileName)) {
                parser.nextToken();
                parsedAge = parser.getIntValue();
            }
            if ("address".equals(fileName)) {
                parser.nextToken();
                while (parser.nextToken() != JsonToken.END_ARRAY) {
                    addresses.add(parser.getText());
                }
            }
        }
        log.debug("name : {}, age:{}, address:{}", parsedName, parsedAge, addresses);
    }

}
