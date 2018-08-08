package com.rinbo.jackson;

import com.fasterxml.jackson.core.*;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Slf4j
public class ObjectMapperTest {

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    static class Car {
        private String type;
        private String color;
    }

    ObjectMapper mapper = new ObjectMapper();

    @Test
    public void testToJson() throws IOException {
        Car car = new Car("yellow", "renault");
        //输出到流中
        mapper.writeValue(System.out, car);
        //输出到文件
        mapper.writeValue(new File("target/car.json"), car);
        //返回结果
        String result = mapper.writeValueAsString(car);
        log.debug("result : {}", result);
    }

    @Test
    public void testToJava() throws IOException {
        String json = "{ \"color\" : \"Black\", \"type\" : \"FIAT\" }";
        Car car = mapper.readValue(json, Car.class);
        log.debug("result : {}", car);
    }

    @Test
    public void testToJsonNode() throws IOException {
        String json = "{ \"color\" : \"Black\", \"type\" : \"FIAT\" }";
        JsonNode jsonNode = mapper.readTree(json);
        log.debug("result : {}", jsonNode);
        String color = jsonNode.get("color").asText();
        log.debug("color : {}", color);
    }

    @Test
    public void testToList() throws IOException {
        String jsonCarArray =
                "[{ \"color\" : \"Black\", \"type\" : \"BMW\" }, { \"color\" : \"Red\", \"type\" : \"FIAT\" }]";
        //转换成数组
        List<Car> listCar = mapper.readValue(jsonCarArray, new TypeReference<List<Car>>() {
        });
        log.debug("result : {}", listCar);
    }

    @Test
    public void testHandlerColloction() throws IOException {
        String jsonCarArray =
                "[{ \"color\" : \"Black\", \"type\" : \"BMW\" }, { \"color\" : \"Red\", \"type\" : \"FIAT\" }]";
        //转换成数组
        mapper.configure(DeserializationFeature.USE_JAVA_ARRAY_FOR_JSON_ARRAY, true);
        Car[] cars = mapper.readValue(jsonCarArray, Car[].class);
        log.debug("result : {}", cars.length);
    }

    @Test
    public void testToMap() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        String json = "{ \"color\" : \"Black\", \"type\" : \"BMW\" }";
        Map<String, Object> map = mapper.readValue(json, new TypeReference<Map<String, Object>>() {
        });
        log.debug("result : {}", map);
    }



    @Test
    public void testConfiguration() throws IOException {
        String jsonString
                = "{ \"color\" : \"Black\", \"type\" : \"Fiat\", \"year\" : \"1970\" }";
        //配置，遇到不知道的字段不抛异常
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        //配置，主键为空不抛异常
        mapper.configure(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES, false);
        //配置，枚举转换成数字
        mapper.configure(DeserializationFeature.FAIL_ON_NUMBERS_FOR_ENUMS, false);
        //由于存在多的字段，所以出错
        Car car = mapper.readValue(jsonString, Car.class);
        log.debug("result : {}", car);
    }

    //自定义序列化类
    class CustomCarSerializer extends StdSerializer<Car> {

        public CustomCarSerializer() {
            this(null);
        }

        protected CustomCarSerializer(Class<Car> t) {
            super(t);
        }

        @Override
        public void serialize(Car value, JsonGenerator gen, SerializerProvider provider) throws IOException {
            gen.writeStartObject();
            gen.writeObjectField("car_brand", value.getType());
            gen.writeEndObject();
        }
    }

    class CustomCarDeserializer extends StdDeserializer<Car> {
        public CustomCarDeserializer() {
            this(null);
        }

        protected CustomCarDeserializer(Class<?> vc) {
            super(vc);
        }

        @Override
        public Car deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
            Car car = new Car();
            ObjectCodec codec = p.getCodec();
            //获取JsonNode
            JsonNode node = codec.readTree(p);

            //获取属性
            JsonNode colorNode = node.get("color");
            String color = colorNode.asText();
            car.setColor(color);
            return car;
        }
    }

    @Test
    public void testCustSerializer() throws JsonProcessingException {
        SimpleModule module = new SimpleModule("CustomCarSerializer", new Version(1, 0, 0, null, null, null));
        module.addSerializer(Car.class, new CustomCarSerializer());
        //注册自定义序列化器
        mapper.registerModule(module);
        Car car = new Car("yellow", "renault");
        String carJson = mapper.writeValueAsString(car);
        log.debug("result : {}", carJson);
    }

    @Test
    public void testCustDesSerializer() throws IOException {
        String json = "{ \"color\" : \"Black\", \"type\" : \"BMW\" }";
        SimpleModule module = new SimpleModule("CustomCarSerializer", new Version(1, 0, 0, null, null, null));
        module.addDeserializer(Car.class, new CustomCarDeserializer());
        //注册反序列号器
        mapper.registerModule(module);
        Car car = mapper.readValue(json, Car.class);
        log.debug("result : {}", car);
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    static class Request {
        private Car car;
        private Date datePurchased;
    }

    @Test
    public void testHandlerDateFormat() throws JsonProcessingException {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm a z");
        //设置时间日期格式
        mapper.setDateFormat(df);
        String carAsString = mapper.writeValueAsString(new Request(new Car("aa","bb"),new Date()));
        log.debug("result : {}", carAsString);
    }
}
