package br.ufrn.kmeans;

import br.ufrn.point.ForkJoinPoint;
import br.ufrn.point.Point;
import br.ufrn.util.CreatePointInterface;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;
import java.util.concurrent.atomic.AtomicInteger;

public class ForkJoinKmeans extends Kmeans {

    private final ForkJoinPool pool;
    int seqThreshold;

    public ForkJoinKmeans(CreatePointInterface createPointInterface, int seqThreshold) {
        super(createPointInterface);
        pool = ForkJoinPool.commonPool();
        this.seqThreshold = seqThreshold;
    }

    /*
     *   Updates the class for each point. Finds the centroid closest to it.
     * */
    protected void updatePointsClasses(Point[] points) {
        UpdateClassesForkJoin updateClassesForkJoin = new UpdateClassesForkJoin(points, 0, points.length);
        pool.invoke(updateClassesForkJoin);
    }


    /*
     *   Update the centroids array, given that the classes array is already updated
     *   Just does the average point for each of the K classes and saves them on centroids
     *  */
    protected void updateCentroids(Point[] points) {
        AtomicInteger[] classCount = new AtomicInteger[centroids.length];
        // reseting the centroids
        for (int i = 0; i < centroids.length; ++i) {
            centroids[i] = createPointInterface.createPoint(new double[points[0].getDim()]);
            classCount[i] = new AtomicInteger(0);
        }

        // array for counting num of points associated to each class
        // i-th class refers to i-th centroid


        // for each point, retrieve its class and increase its counter
        UpdateCentroidsForkJoin updateCentroidsForkJoin = new UpdateCentroidsForkJoin(points, classCount, 0, points.length);
        pool.invoke(updateCentroidsForkJoin);

        // getting middle point
        for (int i = 0; i < classCount.length; ++i) {
            centroids[i].div(classCount[i].get());
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

    private class UpdateClassesForkJoin extends RecursiveAction {

        Point[] points;
        int l, r;

        public UpdateClassesForkJoin(Point[] points, int l, int r) {
            this.points = points;
            this.l = l;
            this.r = r;
        }

        @Override
        protected void compute() {
            int len = r - l;
            if (len < seqThreshold) {
                for (int i = l; i < r; ++i) {
                    classes[i] = points[i].closestTo(centroids);
                }
            } else {
                int mid = l + len / 2;
                UpdateClassesForkJoin firstSubtask = new UpdateClassesForkJoin(this.points, l, mid);
                UpdateClassesForkJoin secondSubtask = new UpdateClassesForkJoin(this.points, mid, r);

                firstSubtask.fork();
                secondSubtask.compute();
                firstSubtask.join();
            }
        }
    }


    private class UpdateCentroidsForkJoin extends RecursiveAction {

        Point[] points;
        AtomicInteger[] classCount;
        int l, r;

        public UpdateCentroidsForkJoin(Point[] points, AtomicInteger[] classCount, int l, int r) {
            this.points = points;
            this.classCount = classCount;
            this.l = l;
            this.r = r;
        }

        @Override
        protected void compute() {
            int len = r - l;
            if (len < seqThreshold) {
                for (int i = l; i < r; ++i) {
                    int pointClass = classes[i];
                    centroids[pointClass].add(points[i]);
                    classCount[pointClass].incrementAndGet();
                }
            } else {
                int mid = l + len / 2;
                ArrayList<UpdateCentroidsForkJoin> tasks = new ArrayList<>();
                UpdateCentroidsForkJoin firstSubtask = new UpdateCentroidsForkJoin(points, classCount, l, mid);
                UpdateCentroidsForkJoin secondSubtask = new UpdateCentroidsForkJoin(points, classCount, mid, r);

                firstSubtask.fork();
                secondSubtask.compute();
                firstSubtask.join();
            }
        }
    }
}
