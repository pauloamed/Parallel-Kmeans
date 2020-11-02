package br.ufrn.point;

import br.ufrn.util.AtomicDouble;

public class ParallelPoint extends Point{

    private AtomicDouble coords[];

    public ParallelPoint(double[] coords) {
        this.coords = new AtomicDouble[coords.length];
        for(int i = 0; i < coords.length; ++i){
            this.coords[i] = new AtomicDouble(coords[i]);
        }
        this.dim = this.coords.length;
    }

    public ParallelPoint(int dim) {
        this.coords = new AtomicDouble[dim];
        for(int i = 0; i < coords.length; ++i){
            this.coords[i] = new AtomicDouble(0);
        }
        this.dim = this.coords.length;
    }


    @Override
    public int closestTo(Point[] points) {
        int closestPoint = 0;
        double minDist = this.distanceTo(points[0]);

        for(int i = 1; i < points.length; ++i){
            double distanceToPoint = this.distanceTo(points[i]);
            if(distanceToPoint < minDist){
                closestPoint = i;
                minDist = distanceToPoint;
            }
        }
        return closestPoint;
    }


    @Override
    public void add(Point p) {
        for(int i = 0; i < this.dim; ++i){
            coords[i].addAndGet(p.getCoord(i));
        }
    }


    @Override
    public double distanceTo(Point p) {
        double sum = 0.0;
        for(int i = 0; i < this.dim; ++i){
            double x = (this.getCoord(i) - p.getCoord(i));
            sum += x * x;
        }
        return sum;
    }


    @Override
    public double getCoord(int i) {
        return coords[i].get();
    }


    @Override
    public void div(int x) {
        for(int i = 0; i < this.dim; ++i){
            coords[i].div(x);
        }
    }
}
