package br.ufrn.point;

public class SequentialPoint extends Point {

    public SequentialPoint(double[] coords) {
        super(coords);
    }
    public SequentialPoint(int dim) {
        super(dim);
    }
    public SequentialPoint(){}
    public SequentialPoint(Point p) {
        super(p);
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

    // no relatorio botar que isso aqui precisa ser testado pq tem estado comparitlhado
    public void add(Point p) {
        for(int i = 0; i < this.dim; ++i){
            coords[i] += p.getCoord(i);
        }
    }

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
        return coords[i];
    }


    public void div(int x) {
        for(int i = 0; i < this.dim; ++i){
            coords[i] /= x;
        }
    }
}
