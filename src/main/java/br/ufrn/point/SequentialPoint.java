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
}
