package br.ufrn.point;

import br.ufrn.util.AtomicDouble;

public class ExecutorPoint extends Point{

    private AtomicDouble[] coords;

    public ExecutorPoint(double[] coords) {
        super(coords);
    }
    public ExecutorPoint(int dim) {
        super(dim);
    }
    public ExecutorPoint(){}
    public ExecutorPoint(Point p) {
        super(p);
    }

    @Override
    public int closestTo(Point[] points) {
        return 0;
    }

    @Override
    public void add(Point p) {

    }

    @Override
    public void div(int x) {

    }

    @Override
    public double distanceTo(Point p) {
        return 0;
    }

    @Override
    public double getCoord(int i) {
        return coords[i].get();
    }
}
