package com.yiuhet.multimedia.tuple;

public class Tuple4<A, B, C, D> extends Tuple3<A, B, C> {
    public final D d;

    public Tuple4(A a, B b, C c, D d) {
        super(a, b, c);
        this.d = d;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Tuple4)) {
            return false;
        }
        Tuple4<?, ?, ?, ?> t = (Tuple4<?, ?, ?, ?>) o;
        return equalsEx(t.a, a) && equalsEx(t.b, b) && equalsEx(t.c, c) && equalsEx(t.d, d);
    }

    @Override
    public int hashCode() {
        return hashCodeEx(a) ^ hashCodeEx(b) ^ hashCodeEx(c) ^ hashCodeEx(d);
    }
}
