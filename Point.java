import java.lang.Math;

public class Point{
    // array guardando valores e classe
    // distance to
    private double coords[];
    private int dim;


    Point(double coords[]){
        this.dim = coords.length;
        this.coords = coords.clone();
    }

    Point(int dim){
        this.dim = dim;
        coords = new double[dim];
    }

    // euclidian distance
    public double distanceTo(Point p){
        double sum = 0.0;
        for(int i = 0; i < this.dim; ++i){
            sum += coords[i];
        }
        return Math.sqrt(sum);
    }

    public int closestTo(Point points[]){
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

    public void add(Point p){
        if(this.dim != p.getDim()){
            throw new RuntimeException();
        }else{
            for(int i = 0; i < this.dim; ++i){
                coords[i] += p.getCoords()[i];
            }
        }
    }

    public getDim(){
        return dim;
    }

    double[] getCoords(){
        return coords;
    }
}
