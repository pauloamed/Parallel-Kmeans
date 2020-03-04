public class Kmeans{

    private ArrayList<KmeansPoint> points;
    private double centroids[];

    Kmeans(){

    }

    private void initCentroids(){

    }

    private void updateCentroids(){
        Point newCentroids[] = new Point[centroids.length](points[0].getDim());
        int classCount[] = new int[centroids.length];

        for(Point p : points){
            newCentroids[p.getCurrentClass()].add(p);
            classCount[p.getCurrentClass()]++;
        }


        for(int i = 0; i < classCount.length; ++i){
            newCentroids[i].intDiv(classCount[i]);
        }

        centroids = newCentroids.clone();
    }

    private void updatePointsClasses(){
        for(Point p : points){
            p.setCurrentClass(p.closestTo(centroids));
        }
    }

    public void run(ArrayList<RawPoint> startPoints, int K){
        if(K <= 1){
            return;
        }

        this.centroids = new double[K];
        initCentroids();
        // define centroid classes
        while(true){
            updatePointsClasses();
            updateCentroids();
        }
    }
}
