package br.ufrn.point;

public class SyncronizedPoint extends Point {

    private final double[] coords;

    public SyncronizedPoint(double[] coords) {
        this.dim = coords.length;
        this.coords = coords.clone();
    }

    public SyncronizedPoint(int dim) {
        this.dim = dim;
        coords = new double[dim];
    }


    @Override
    public int closestTo(Point[] points) {
        int closestPoint = 0;
        double minDist = this.distanceTo(points[0]);

        for (int i = 1; i < points.length; ++i) {
            double distanceToPoint = this.distanceTo(points[i]);
            if (distanceToPoint < minDist) {
                closestPoint = i;
                minDist = distanceToPoint;
            }
        }
        return closestPoint;
    }

    @Override
    public synchronized void add(Point p) {
        for(int i = 0; i < p.dim; ++i){
            coords[i] += p.getCoord(i);
        }
    }

    @Override
    public void div(int x) {
        for(int i = 0; i < dim; ++i){
            coords[i] /= x;
        }
    }

    @Override
    public double distanceTo(Point p) {
        double sum = 0.0;
        for(int i = 0; i < dim; ++i){
            double x = p.getCoord(i) - getCoord(i);
            sum += x * x;
        }
        return sum;
    }

    @Override
    public double getCoord(int i) {
        return coords[i];
    }
}
