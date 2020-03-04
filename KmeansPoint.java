public class KmeansPoint extends Point{
    // array guardando valores e classe
    // distance to

    public int currentClass;

    KmeansPoint(double coords[]){
        this.super(coords);
        currentClass = -1;
    }

    KmeansPoint(Point p){
        this.coords = p.coords.clone();
        currentClass = -1;
    }

    public int getCurrentClass(){
        return currentClass;
    }

    public void setCurrentClass(int x){
        currentClass = x;
    }
}
