package br.ufrn.point;

import br.ufrn.util.AtomicDouble;

import java.util.OptionalDouble;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class StreamPoint extends Point{

    private AtomicDouble coords[];

    public StreamPoint(double[] coords) {
        this.dim = coords.length;
        this.coords = new AtomicDouble[coords.length];
        for(int i = 0; i < coords.length; ++i){
            this.coords[i] = new AtomicDouble(coords[i]);
        }
    }

    public StreamPoint(int dim) {
        this.dim = dim;
        this.coords = new AtomicDouble[dim];
        for(int i = 0; i < coords.length; ++i){
            this.coords[i] = new AtomicDouble(0);
        }
    }

    @Override
    public int closestTo(Point[] points) {
        return IntStream.range(0,points.length)
                .reduce((a, b) -> {
                    double distanceToA = this.distanceTo(points[a]);
                    double distanceToB = this.distanceTo(points[b]);
                    return (distanceToA < distanceToB)? a : b;
                })
                .getAsInt();
    }

    @Override
    public void add(Point p) {
        IntStream.range(0, p.getDim()).parallel().forEach(i -> {
            this.coords[i].addAndGet(p.getCoord(i));
        });
    }

    @Override
    public void div(int x) {
        IntStream.range(0, this.getDim()).parallel().forEach(i -> {
            coords[i].div(x);
        });
    }

    @Override
    public double distanceTo(Point p) {
        return IntStream.range(0, p.getDim())
                .parallel()
                .mapToDouble(i -> {
                    double x = (this.getCoord(i) - p.getCoord(i));
                    return x * x;
                })
                .sum();
    }

    @Override
    public double getCoord(int i) {
        return coords[i].get();
    }
}
