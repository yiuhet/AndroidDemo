package com.yiuhet.multimedia.tuple;

public final class TupleUtil {
    private TupleUtil() {
        throw new AssertionError();
    }

    public static <A, B> Tuple<A, B> tuple(A a, B b) {
        return new Tuple<A, B>(a, b);
    }

    public static <A, B, C> Tuple3<A, B, C> tuple(A a, B b, C c) {
        return new Tuple3<A, B, C>(a, b, c);
    }

    public static <A, B, C, D> Tuple4<A, B, C, D> tuple(A a, B b, C c, D d) {
        return new Tuple4<A, B, C, D>(a, b, c, d);
    }

}
