package com.rinbo.java8.stream;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class RuleEngine5 {
    @Test
    public void whenNumbersGivenToRuleEngine_thenReturnCorrectResult() {
        Expression expression = new Expression(5, 5, Operator.ADD);
        RuleEngine engine = new RuleEngine();
        Result result = engine.process(expression);
        log.debug("result is : {}",result.getValue());
    }
}

interface Rule {
    boolean evaluate(Expression expression);

    Result getResult();
}

@Data
@AllArgsConstructor
class Expression {
    private Integer x;
    private Integer y;
    private Operator operator;
}

class Result {
    int value;

    public Result(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}

class AddRule implements Rule {

    private int result;

    @Override
    public boolean evaluate(Expression expression) {
        boolean evalResult = false;
        if (expression.getOperator() == Operator.ADD) {
            this.result = expression.getX() + expression.getY();
            evalResult = true;
        }
        return evalResult;
    }

    @Override
    public Result getResult() {
        return new Result(result);
    }
}

class RuleEngine {

    private static List<Rule> rules = new ArrayList<>();
    static {
        rules.add(new AddRule());
    }

    public Result process(Expression expression) {
        Rule rule = rules.stream()
                .filter(r -> r.evaluate(expression))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Expression does not matches any Rule"));
        return rule.getResult();
    }
}