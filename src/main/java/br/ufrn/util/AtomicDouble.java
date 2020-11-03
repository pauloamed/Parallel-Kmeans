package br.ufrn.util;

import java.util.concurrent.atomic.AtomicLong;

import static java.lang.Double.doubleToLongBits;
import static java.lang.Double.longBitsToDouble;

public class AtomicDouble {

    private final AtomicLong bits;

    public AtomicDouble() {
        this(0f);
    }

    public AtomicDouble(double initialValue) {
        bits = new AtomicLong(doubleToLongBits(initialValue));
    }


    public final double get() {
        return longBitsToDouble(bits.get());
    }

    public final double getAndSet(double newValue) {
        return longBitsToDouble(bits.getAndSet(doubleToLongBits(newValue)));
    }

    public final boolean compareAndSet(double expect, double newValue) {
        return bits.compareAndSet(doubleToLongBits(expect),
                doubleToLongBits(newValue));
    }

    public final double addAndGet(double value) {
        double curr;
        do {
            curr = get();
        } while (!compareAndSet(curr, curr + value));
        return curr + value;
    }

    public void div(int x) {
        double curr;
        do {
            curr = get();
        } while (!compareAndSet(curr, curr / x));
    }
}