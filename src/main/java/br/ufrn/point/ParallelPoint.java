package br.ufrn.point;

import br.ufrn.util.PointIndex;

import java.util.*;


public class ParallelPoint extends Point{

    private int numThreads;

    public ParallelPoint(double[] coords, int numThreads) {
        super(coords);
        this.numThreads = numThreads;
    }

    ParallelPoint(int dim, int numThreads) {
        super(dim);
        this.numThreads = numThreads;
    }

    public int closestTo(Point[] points) throws InterruptedException {
        PointIndex[] pointsIndexes = new PointIndex[points.length];

        Arrays.asList(points)
                .stream()
                .parallel()
                .min((a, b) -> {
                    double distA = this.distanceTo(a);
                    double distB = this.distanceTo(b);
                    if(distA < distB) return -1;
                    else if(distA == distB) return 0;
                    else return 1;
                })
                .get();
        return -1;
    }


    public int a(Point[] points) throws InterruptedException {
        ClosestToThread threads[] = new ClosestToThread[numThreads];

        int sliceSize = points.length / numThreads;
        for(int i = 0; i < numThreads; ++i){
            int start = i * sliceSize;
            int end = ((i + 1) * sliceSize);
            if(i == numThreads - 1) end = points.length;
            threads[i] = new ClosestToThread();
            threads[i].run(points, start, end);
        }

        int closestPoint = -1;
        double closestPointDistance = -1;
        for(int i = 0; i < numThreads; ++i){
            threads[i].join();
            if(closestPointDistance == -1 || threads[i].getClosestPointDistance() <= closestPointDistance){
                closestPointDistance = threads[i].getClosestPointDistance();
                closestPoint = threads[i].getClosestPoint();
            }
        }
        return closestPoint;
    }


    private class ClosestToThread extends Thread {

        private int closestPoint;
        private double closestPointDistance;

        public ClosestToThread() {
            this.closestPoint = -1;
            this.closestPointDistance = -1;
        }

        public void run(Point[] points, int start, int end) {
            double closestPointDistance = ParallelPoint.this.distanceTo(points[start]);
            closestPoint = start;

            for(int i = start; i < end; ++i){
                double distanceToPoint = ParallelPoint.this.distanceTo(points[i]);
                if(distanceToPoint < closestPointDistance){
                    this.closestPoint = i;
                    closestPointDistance = distanceToPoint;
                }
            }
        }

        public int getClosestPoint(){return closestPoint;};

        public double getClosestPointDistance() {
            return closestPointDistance;
        }
    }


}
