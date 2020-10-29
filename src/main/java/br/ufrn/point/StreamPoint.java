package br.ufrn.point;

import br.ufrn.util.AtomicDouble;

import java.util.OptionalDouble;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class StreamPoint extends Point{

    private AtomicDouble coords[];

    public StreamPoint(double[] coords) {
        super(coords);
    }
    public StreamPoint(int dim) {
        super(dim);
    }
    public StreamPoint(){}
    public StreamPoint(Point p) {
        super(p);
    }

    @Override
    public int closestTo(Point[] points) {
        return IntStream.range(0,points.length)
                .parallel()
                .reduce((a, b) -> {
                    double distanceToA = this.distanceTo(points[a]);
                    double distanceToB = this.distanceTo(points[b]);
                    return (distanceToA < distanceToB)? a : b;
                })
                .getAsInt();  // or throw
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
            while(true){
                double curr = this.coords[i].get();
                if(this.coords[i].compareAndSet(curr, curr / x)) {
                    return;
                }
            }
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
                .reduce((x, y) -> {
                    return x + y;
                })
                .getAsDouble();
    }

    @Override
    public double getCoord(int i) {
        return coords[i].get();
    }
}
