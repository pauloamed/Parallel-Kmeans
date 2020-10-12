package br.ufrn.kmeans;

import br.ufrn.point.ParallelPoint;
import br.ufrn.point.Point;
import br.ufrn.point.SequentialPoint;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.atomic.AtomicInteger;

public class ParallelKmeans extends Kmeans{

    private int numThreads;
    private CyclicBarrier barriers[];
    private AtomicInteger classCount[];

    public ParallelKmeans(int numThreads) {
        this.numThreads = numThreads;
        this.barriers = new CyclicBarrier[4];
        for(int i = 0; i < barriers.length; ++i){
            this.barriers[i] = new CyclicBarrier(numThreads + 1);
        }
    }

    /*
        -- LOOP START --
        BARRIER 0
        PHASE 1: Workers compute the correspondent class for each point. No shared state here
        BARRIER 1
        PHASE 2: Main inits aux. structures for PHASE 3.
        BARRIER 2
        PHASE 3: Workers compute sum of points for each centroid and classCount. Both centroids
        and classCounts are shared states.
        BARRIER 3
        PHASE 4: Main compute average for centroids using classCounts and shared states
        -- LOOP END --
     */


    public int[] run(Point[] points, int K, int numIterations) throws InterruptedException, BrokenBarrierException {
        WorkerThread workers[] = new WorkerThread[numThreads];

        this.centroids = new Point[K]; // centroids class (K)
        this.classes = new int[points.length]; // classes for each point (N)

        initCentroids(points, K);

        int sliceSize = points.length / numThreads;
        // creates workers and inits their responsabilites ranges
        for(int i = 0; i < numThreads; ++i){
            int start = i * sliceSize;
            int end = ((i + 1) * sliceSize);
            if(i == numThreads - 1) end = points.length;
            workers[i] = new WorkerThread(start, end, numIterations, points, "Worker " + i);
            workers[i].start();
        }

        for(int iter = 0; iter < numIterations; ++iter){
            System.out.println("Main; Iter: " + iter);
            barriers[0].await(); // init wait
            // PHASE 0
            // workers updating classes
            barriers[1].await();
            // PHASE 1
            // array for counting occourences for each of the classes
            classCount= new AtomicInteger[centroids.length];
            // PHASE 2
            // reseting the centroids, init for next phase
            for(int i = 0; i < centroids.length; ++i){
                centroids[i] = new SequentialPoint(new double[centroids[0].getDim()]);
                classCount[i] = new AtomicInteger(0);
            }
            barriers[2].await();
            // PHASE 3
            // workers computing sum for each centroid
            barriers[3].await();
            // PHASE 4
            for(int i = 0; i < classCount.length; ++i){
                centroids[i].div(classCount[i].get());
            }
        }

        // waits for workers to finish
        for(int i = 0; i < numThreads; ++i){
            workers[i].join();
        }

        return this.classes;
    }

    private class WorkerThread extends Thread {

        int start, end, numItearations;
        Point[] points;

        public WorkerThread(int start, int end, int numIterations, Point[] points, String name) {
            super(name);
            this.start = start;
            this.end = end;
            this.numItearations = numIterations;
            this.points = points;
        }

        public void run(){
            System.out.println("Thread " + this.getName() + " started; Start: " + this.start + "; End: " + this.end);
            for(int iter = 0; iter < numItearations; ++iter){
                try {
                    System.out.println("Thread " + this.getName() + "; Iter: " + iter);
                    barriers[0].await(); // init loop barrier

                    // updating classes for each point
                    for(int i = start; i < end; i++){
                       classes[i] = points[i].closestTo(centroids);
                    }

                    barriers[1].await();
                    // main method initing aux structures
                    barriers[2].await();

                    // updates centroids
                    for(int i = start; i < end; i++){
                        int pointClass = ParallelKmeans.this.classes[i];
                        centroids[pointClass].add(points[i]);
                        classCount[pointClass].incrementAndGet();
                    }

                    barriers[3].await();
                    // main method computing middle points
                } catch (InterruptedException | BrokenBarrierException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
