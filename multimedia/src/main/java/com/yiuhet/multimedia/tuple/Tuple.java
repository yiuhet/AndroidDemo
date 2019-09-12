package com.yiuhet.multimedia.tuple;

public class Tuple<A, B> {
    public final A a;
    public final B b;

    public Tuple(A a, B b) {
        this.a = a;
        this.b = b;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Tuple)) {
            return false;
        }
        Tuple<?, ?> t = (Tuple<?, ?>) o;
        return equalsEx(t.a, a) && equalsEx(t.b, b);
    }

    @Override
    public int hashCode() {
        return hashCodeEx(a) ^ hashCodeEx(b);
    }

    boolean equalsEx(Object a, Object b) {
        return a == b || (a != null && a.equals(b));
    }

    int hashCodeEx(Object o) {
        return o == null ? 0 : o.hashCode();
    }
}
