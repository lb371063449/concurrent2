package com.rinbo.java8.stream;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

//walk through the various ways of replacing nested if statements.
//替换if语句
@Slf4j
public class ReplaceManyIf4 {

    public int calculate(int a, int b, String operator) {
        int result = Integer.MIN_VALUE;

        if ("add".equals(operator)) {
            result = a + b;
        } else if ("multiply".equals(operator)) {
            result = a * b;
        } else if ("divide".equals(operator)) {
            result = a / b;
        } else if ("subtract".equals(operator)) {
            result = a - b;
        } else if ("modulo".equals(operator)) {
            result = a % b;
        }
        return result;
    }

    public int calculateUsingSwitch(int a, int b, String operator) {
        int result = 0;
        switch (operator) {
            case "add":
                result = a + b;
                break;
            case "multiply":
                result = a * b;
                break;
            case "divide":
                result = a / b;
                break;
            case "subtract":
                result = a - b;
                break;
            case "modulo":
                result = a % b;
                break;
            default:
                result = Integer.MIN_VALUE;
        }
        return result;
    }

    public int calculateUsingSwitch(int a, int b, Operator operator) {
        int result = 0;
        switch (operator) {
            case ADD:
                result = a + b;
                break;
            case MULTIPLY:
                result = a * b;
                break;
            case DIVIDE:
                result = a / b;
                break;
            case SUBTRACT:
                result = a - b;
                break;
            case MODULO:
                result = a % b;
                break;
            default:
                result = Integer.MIN_VALUE;
        }
        return result;
    }

    public int calculate(int a, int b, Operator operator) {
        return operator.apply(a, b);
    }

    public int calculateUsingFactory(int a, int b, String operation) {
        Operation targetOperation = OperatorFactory.getOperation(operation)
                .orElseThrow(() -> new IllegalArgumentException("Invalid Operator"));
        return targetOperation.apply(a, b);
    }

//    public int calculate(Command command) {
//        return command.execute();
//    }

    @Test
    public void test1() {
        ReplaceManyIf4 replaceManyIf4 = new ReplaceManyIf4();
        int add = replaceManyIf4.calculateUsingFactory(2, 1, "add");
        log.debug("add is : {}",add);
    }

    @Test
    public void whenCalculateUsingEnumOperator_thenReturnCorrectResult() {
        ReplaceManyIf4 replaceManyIf4 = new ReplaceManyIf4();
        int result = replaceManyIf4.calculate(3, 4, Operator.valueOf("ADD"));
        log.debug("add is : {}",result);
    }
}

//1、定义Factory Class
interface Operation {
    int apply(int a, int b);
}

//2、定义Factory Class实现类
class Addition implements Operation {
    @Override
    public int apply(int a, int b) {
        return a + b;
    }
}

class Division implements Operation {
    @Override
    public int apply(int a, int b) {
        return a / b;
    }
}

//4、 implement a factory class which returns instances of Operation based on the given operator
class OperatorFactory {
    static Map<String, Operation> operationMap = new HashMap<>();

    static {
        operationMap.put("add", new Addition());
        operationMap.put("divide", new Division());
    }

    public static Optional<Operation> getOperation(String operator) {
        return Optional.ofNullable(operationMap.get(operator));
    }
}

//1、定义操作
enum Operator {

    ADD {
        @Override
        public int apply(int a, int b) {
            return a + b;
        }
    },

    MULTIPLY {
        @Override
        public int apply(int a, int b) {
            return a * b;
        }
    },

    SUBTRACT {
        @Override
        public int apply(int a, int b) {
            return a - b;
        }
    },

    DIVIDE {
        @Override
        public int apply(int a, int b) {
            return a / b;
        }
    },

    MODULO {
        @Override
        public int apply(int a, int b) {
            return a % b;
        }
    };

    public abstract int apply(int a, int b);
}