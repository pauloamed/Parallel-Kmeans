package br.ufrn.kmeans;

import br.ufrn.point.ParallelPoint;
import br.ufrn.point.Point;
import br.ufrn.point.SequentialPoint;
import br.ufrn.point.StreamPoint;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

public class StreamKmeans extends Kmeans{


    public StreamKmeans() {
    }

    /*
     *   Update the centroids array, given that the classes array is already updated
     *   Just does the average point for each of the K classes and saves them on centroids
     *  */
    protected void updateCentroids(Point[] points){
        // reseting the centroids
        // array for counting num of points associated to each class
        // i-th class refers to i-th centroid
        AtomicInteger[] classCount = new AtomicInteger[centroids.length];
        for(int i = 0; i < centroids.length; ++i){
            classCount[i] = new AtomicInteger(0);
            centroids[i] = new StreamPoint(points[0].getDim());
        }

        // for each point, retrieve its class and increase its counter

        IntStream.range(0, points.length).parallel().forEach(i -> {
            int pointClass = this.classes[i];
            centroids[pointClass].add(points[i]);
            classCount[pointClass].incrementAndGet();
        });

        IntStream.range(0, centroids.length).parallel().forEach(i -> {
            centroids[i].div(classCount[i].get());
        });
    }

    public int[] run(Point[] points, int K, int numIterations){

        // algorithm wont run for K <= 1 or K > N
        if(K <= 1 || K > points.length){
            throw new RuntimeException();
        }

        this.centroids = new StreamPoint[K]; // centroids class (K)
        this.classes = new int[points.length]; // classes for each point (N)

        // initing centroids
        initCentroids(points, K);


        // runs algo for numIterations steps
        for(int iter = 0; iter < numIterations; iter++){
            IntStream.range(0, points.length).parallel().forEach(i -> {
                classes[i] = points[i].closestTo(centroids);
            });
            updateCentroids(points); // then centroids
        }

        // returns classes for each point
        return this.classes;
    }
}
