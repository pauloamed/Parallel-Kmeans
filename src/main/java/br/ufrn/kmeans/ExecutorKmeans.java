package br.ufrn.kmeans;

import br.ufrn.point.Point;

import java.util.concurrent.BrokenBarrierException;

public class ExecutorKmeans extends Kmeans{
    @Override
    public int[] run(Point[] points, int K, int numIterations){
        return new int[0];
    }
}
