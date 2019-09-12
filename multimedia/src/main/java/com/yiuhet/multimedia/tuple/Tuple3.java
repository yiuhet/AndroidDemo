package com.yiuhet.multimedia.tuple;


public class Tuple3<A, B, C> extends Tuple<A, B> {
    public final C c;

    public Tuple3(A a, B b, C c) {
        super(a, b);
        this.c = c;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Tuple3)) {
            return false;
        }
        Tuple3<?, ?, ?> t = (Tuple3<?, ?, ?>) o;
        return equalsEx(t.a, a) && equalsEx(t.b, b) && equalsEx(t.c, c);
    }

    @Override
    public int hashCode() {
        return hashCodeEx(a) ^ hashCodeEx(b) ^ hashCodeEx(c);
    }
}
