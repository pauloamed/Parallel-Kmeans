package br.ufrn.util;

import java.util.concurrent.atomic.AtomicLong;

import static java.lang.Double.*;

public class AtomicDouble {

    private AtomicLong bits;

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

    public final boolean compareAndSet(double expect, double update) {
        return bits.compareAndSet(doubleToLongBits(expect),
                doubleToLongBits(update));
    }

    public final double addAndGet(double value){
        return longBitsToDouble(bits.addAndGet(doubleToLongBits(value)));
    }

}