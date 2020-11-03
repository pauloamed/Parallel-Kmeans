package br.ufrn.util;

public class PairDoubleInt {
    public int y;
    public double x;

    public PairDoubleInt(double x, int y) {
        this.x = x;
        this.y = y;
    }

    public static PairDoubleInt merge(PairDoubleInt a, PairDoubleInt b) {
        return (a.x < b.x) ? a : b;
    }
}
