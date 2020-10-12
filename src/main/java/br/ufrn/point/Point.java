package br.ufrn.point;

import java.io.Serializable;

import static java.lang.Math.abs;

public abstract class Point implements Serializable {
    // array guardando valores e classe
    // distance to
    protected final double[] coords;
    protected final int dim;
    private final double EPS = 0.001;

    public Point(double coords[]){
        this.dim = coords.length;
        this.coords = coords.clone();
    }

    public Point(int dim){
        this.dim = dim;
        coords = new double[dim];
    }

    public Point(){
        this.dim = 1;
        coords = new double[1];
    }

    public Point(Point p){
        this.dim = p.dim;
        this.coords = p.coords.clone();
    }

    public abstract int closestTo(Point points[]) throws InterruptedException;

    public static double distanceBetween(Point a, Point b){
        double sum = 0.0;
        for(int i = 0; i < a.dim; ++i){
            double x = (a.coords[i] - b.coords[i]);
            sum += x * x;
        }
        return sum;
    }

    // no relatorio botar que isso aqui precisa NAO PRECISO SER testado pq NAO tem estado comparitlhado
    public double distanceTo(Point p) {
        return distanceBetween(this, p);
    }

    // no relatorio botar que isso aqui precisa ser testado pq tem estado comparitlhado
    public synchronized void add(Point p) {
        if(this.dim != p.getDim()){
            throw new RuntimeException(this.dim + " " + p.getDim());
        }else{
            for(int i = 0; i < this.dim; ++i){
                coords[i] += p.getCoord(i);
            }
        }
    }


    public synchronized void div(int x) {
        for(int i = 0; i < this.dim; ++i){
            coords[i] /= x;
        }
    }

    public String toString() {
        StringBuilder ret = new StringBuilder("(");
        for(int i = 0; i < this.dim; ++i){
            ret.append(this.coords[i]);
            if(i < this.dim - 1) ret.append(", ");
        }
        ret.append(")");
        return ret.toString();
    }

    public int getDim(){
        return dim;
    }

    public double[] getCoords(){
        return coords;
    }

    public double getCoord(int i){
        return coords[i];
    }

    public boolean equalsTo(Point p){
        if(this.dim != p.dim) return false;
        for(int i = 0; i < dim; i++){
            if(abs(this.coords[i] - p.coords[i]) > EPS){
                return false;
            }
        }
        return true;
    }
}
