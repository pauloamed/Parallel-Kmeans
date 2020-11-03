package br.ufrn.kmeans;

import br.ufrn.point.Point;

import java.util.HashSet;
import java.util.Random;

public abstract class Kmeans {

    /*
     *   Array containing the classes of all points
     *   The class of a point is a value between 0 and K-1
     *   Size of array: N
     * */
    protected int[] classes;

    /*
     *   Array containing the points representing the classes centroids
     *   Size of array: K
     * */
    protected Point[] centroids;

    public Kmeans() {
    }

    /*
     *   Init the centroids points with a random subset of the original
     *  */
    protected void initCentroids(Point[] points, int K) {
        Random randomGen = new Random(7); // random int gen
        HashSet<Integer> hashSet = new HashSet<Integer>(); // wont repeat points as centroids
        hashSet.add(-1); // adding base case
        for (int i = 0; i < K; i++) {
            int centroidId = -1; // base case
            while (hashSet.contains(centroidId)) { // while num is already used as centroid
                centroidId = randomGen.nextInt(points.length); // random num between 0 and N
            }
            centroids[i] = points[centroidId]; // uses point as centroid
            hashSet.add(centroidId); // adds point set of already used points
        }
    }

    /*
     *   Runs algorithm for an array points, using K clusters and for numIterations
     *  */
    public abstract int[] run(Point[] points, int K, int numIterations);
}
