package br.ufrn.kmeans;

import br.ufrn.point.Point;
import br.ufrn.point.SequentialPoint;

public class SequentialKmeans extends Kmeans {

    public SequentialKmeans() {
    }

    /*
     *   Updates the class for each point. Finds the centroid closest to it.
     * */
    protected void updatePointsClasses(Point[] points) {
        for (int i = 0; i < points.length; ++i) {
            classes[i] = points[i].closestTo(centroids);
//            System.out.println("class: " + i + " " + classes[i]);
        }
    }


    /*
     *   Update the centroids array, given that the classes array is already updated
     *   Just does the average point for each of the K classes and saves them on centroids
     *  */
    protected void updateCentroids(Point[] points) {
        // reseting the centroidsSequentialPoint
        for (int i = 0; i < centroids.length; ++i) {
            centroids[i] = new SequentialPoint(new double[points[0].getDim()]);
        }

        // array for counting num of points associated to each class
        // i-th class refers to i-th centroid
        int[] classCount = new int[centroids.length];

        // for each point, retrieve its class and increase its counter
        for (int i = 0; i < points.length; ++i) {
            int pointClass = this.classes[i];
            centroids[pointClass].add(points[i]);
//            System.out.println(i + " " + centroids[pointClass]);
            classCount[pointClass]++;
        }

        // getting middle point
        for (int i = 0; i < classCount.length; ++i) {
            centroids[i].div(classCount[i]);
//            System.out.println("cent_val: " + i + " " + centroids[i]);
        }
    }

    public int[] run(Point[] points, int K, int numIterations) {

        // algorithm wont run for K <= 1 or K > N
        if (K <= 1 || K > points.length) {
            throw new RuntimeException();
        }

        this.centroids = new Point[K]; // centroids class (K)
        this.classes = new int[points.length]; // classes for each point (N)

        // initing centroids
        initCentroids(points, K);

        // runs algo for numIterations steps
        for (int iter = 0; iter < numIterations; iter++) {
            updatePointsClasses(points); // first update classes
            updateCentroids(points); // then centroids
        }

        // returns classes for each point
        return this.classes;
    }
}
