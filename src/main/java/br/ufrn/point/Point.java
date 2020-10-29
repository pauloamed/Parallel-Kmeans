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

    public abstract int closestTo(Point points[]);
    public abstract void add(Point p);
    public abstract void div(int x);
    public abstract double distanceTo(Point p);
    public abstract double getCoord(int i);

    // no relatorio botar que isso aqui precisa NAO PRECISO SER testado pq NAO tem estado comparitlhado



    public String toString() {
        StringBuilder ret = new StringBuilder("(");
        for(int i = 0; i < this.dim; ++i){
            ret.append(this.getCoord(i));
            if(i < this.dim - 1) ret.append(", ");
        }
        ret.append(")");
        return ret.toString();
    }

    public int getDim(){
        return dim;
    }


    public boolean equalsTo(Point p){
        if(this.dim != p.dim) return false;
        for(int i = 0; i < dim; i++){
            if(abs(this.getCoord(i) - p.getCoord(i)) > EPS){
                return false;
            }
        }
        return true;
    }
}
