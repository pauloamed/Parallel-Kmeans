package br.ufrn.kmeans;

import br.ufrn.point.ExecutorPoint;
import br.ufrn.point.Point;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static br.ufrn.util.ExecutorServiceSingleton.getExec;

public class ExecutorKmeans extends Kmeans {

    private static ExecutorService exec;

    public ExecutorKmeans() {
        exec = getExec();
    }

    /*
     *   Updates the class for each point. Finds the centroid closest to it.
     * */
    protected void updatePointsClasses(Point[] points) throws InterruptedException {
        List<Callable<Object>> tasks = new ArrayList<>();
        for (int i = 0; i < points.length; ++i) {
            final int finalI = i;
            Runnable task = () -> {
                classes[finalI] = points[finalI].closestTo(centroids);
            };
            tasks.add(Executors.callable(task));
        }
        exec.invokeAll(tasks);
    }

    /*
     *   Update the centroids array, given that the classes array is already updated
     *   Just does the average point for each of the K classes and saves them on centroids
     *  */
    protected void updateCentroids(Point[] points) throws InterruptedException {
        // reseting the centroidsSequentialPoint
        for (int i = 0; i < centroids.length; ++i) {
            centroids[i] = new ExecutorPoint(points[0].getDim());
        }

        // array for counting num of points associated to each class
        // i-th class refers to i-th centroid
        AtomicInteger[] classCount = new AtomicInteger[centroids.length];

        // for each point, retrieve its class and increase its counter
        List<Callable<Object>> tasks = new ArrayList<>();
        for (int i = 0; i < points.length; ++i) {
            final int finalI = i;
            Runnable task = () -> {
                int pointClass = classes[finalI];
                centroids[pointClass].add(points[finalI]);
//                    System.out.println(finalI + " " + centroids[pointClass]);
                classCount[pointClass].incrementAndGet();
            };
            tasks.add(Executors.callable(task));
        }
        exec.invokeAll(tasks);

        tasks.clear();
        // getting middle point
        for (int i = 0; i < classCount.length; ++i) {
            int finalI = i;
            Runnable task = () -> {
                    centroids[finalI].div(classCount[finalI].get());
            };
            tasks.add(Executors.callable(task));
        }
        exec.invokeAll(tasks);
    }


    public int[] run(Point[] points, int K, int numIterations) {

        // algorithm wont run for K <= 1 or K > N
        if (K <= 1 || K > points.length) {
            throw new RuntimeException();
        }



        this.centroids = new ExecutorPoint[K]; // centroids class (K)
        this.classes = new int[points.length]; // classes for each point (N)

        // initing centroids
        initCentroids(points, K);


        // runs algo for numIterations steps
        try {
            for (int iter = 0; iter < numIterations; iter++) {
                updatePointsClasses(points);
                updateCentroids(points);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        exec.shutdown();

        // returns classes for each point
        return this.classes;
    }
}
