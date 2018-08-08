package com.rinbo.reactor.operations;


import org.junit.Test;
import reactor.core.publisher.Flux;

public class OperationTest1 {

    @Test
    public void testFlatMap() {
        Flux.just("hello","word","!!")
                .flatMap(i->Flux.fromArray(i.split("")))
//                .zipWith(Flux.range(1,12),  (string, count) -> string.format("%2d. %s", count, string))
                .subscribe(System.out::println);
    }
}
