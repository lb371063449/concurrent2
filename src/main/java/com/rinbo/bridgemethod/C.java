package com.rinbo.bridgemethod;

public abstract class C<T> {
    public abstract T id(T x);
}

class D extends C<String>{

    @Override
    public String id(String x) {
        return x + ":id";
    }
}
