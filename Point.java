import java.lang.Math;

public class Point{
    // array guardando valores e classe
    // distance to
    public double coords[];
    private int dim;


    Point(double coords[]){
        this.dim = coords.length;
        this.coords = coords.clone();
    }

    Point(int dim){
        this.dim = dim;
        coords = new double[dim];
    }

    double[] getCoords(){
        return coords;
    }

    public void add(Point p){
        if(this.dim != p.getDim()){

        }else{
            for(int i = 0; i < this.coords.length; ++i){
                coords[i] += p.getCoords()[i];
            }
        }
    }

    Point

    public getDim(){
        return dim;
    }

    // euclidian distance
    public double distanceTo(Point p){
        double sum = 0.0;
        for(int i = 0; i < coords.length; ++i){
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
}
