import java.util.Random;

public class Kmeans{

    private int classes[];
    private Point centroids[];

    Kmeans(){

    }

    private void initCentroids(int K){
        Random randomGen = new Random();

        for(int i = 0; i < K; i++){
            int centroidId = randomGen.nextInt(points.length());
            centroids[i] = points.get(centroidId);
        }
    }

    private void updateCentroids(){
        Point newCentroids[] = new Point[centroids.length](points[0].getDim());
        int classCount[] = new int[centroids.length];

        int i = 0;
        for(Point p : points){
            int pointClass = classes[i];

            newCentroids[pointClass].add(p);
            classCount[pointClass]++;

            i++;
        }


        for(int i = 0; i < classCount.length; ++i){
            newCentroids[i].intDiv(classCount[i]);
        }

        centroids = newCentroids.clone();
    }

    private void updatePointsClasses(){
        int i = 0;
        for(Point p : points){
            classes[i] = p.closestTo(centroids);

            i++;
        }
    }

    public void run(ArrayList<Point> points, int K){
        if(K <= 1){
            return;
        }

        this.centroids = new double[K];
        this.classes = new int[points.length()];

        initCentroids();
        // define centroid classes

        int i = 0;
        while(i < 10){
            updatePointsClasses();
            updateCentroids();

            i++;
        }

        return classes;
    }
}
