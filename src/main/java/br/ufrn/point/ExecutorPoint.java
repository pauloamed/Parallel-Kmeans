package br.ufrn.point;

import br.ufrn.util.AtomicDouble;
import br.ufrn.util.PairDoubleInt;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import static br.ufrn.util.ExecutorServiceSingleton.getExec;

public class ExecutorPoint extends Point {

    private final AtomicDouble[] coords;
    private ExecutorService exec;

    public ExecutorPoint(double[] coords) {
        this.dim = coords.length;
        this.coords = new AtomicDouble[coords.length];
        for (int i = 0; i < coords.length; ++i) {
            this.coords[i] = new AtomicDouble(coords[i]);
        }
        exec = getExec();
    }

    public ExecutorPoint(int dim) {
        this.dim = dim;
        this.coords = new AtomicDouble[dim];
        for (int i = 0; i < coords.length; ++i) {
            this.coords[i] = new AtomicDouble(0);
        }
        exec = getExec();
    }


    @Override
    public int closestTo(Point[] points) {
        int closestPoint = 0;

        List<Callable<PairDoubleInt>> tasks = new ArrayList<Callable<PairDoubleInt>>();
        for (int i = 0; i < points.length; ++i) {
            int finalI = i;
            Callable<PairDoubleInt> task = () -> {
                return new PairDoubleInt(distanceTo(points[finalI]), finalI);
            };
            tasks.add(task);
        }

        List<Future<PairDoubleInt>> futures;

        try {
            futures = exec.invokeAll(tasks);
            double minDist = Double.POSITIVE_INFINITY;
            for (Future<PairDoubleInt> future : futures) {
                if (future.get().x < minDist) {
                    minDist = future.get().x;
                    closestPoint = future.get().y;
                }
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        return closestPoint;
    }


    @Override
    public void add(Point p) {
        for (int i = 0; i < this.dim; ++i) {
            coords[i].addAndGet(p.getCoord(i));
        }
    }


    @Override
    public double distanceTo(Point p) {
        double sum = 0.0;
        for (int i = 0; i < this.dim; ++i) {
            double x = (this.getCoord(i) - p.getCoord(i));
            sum += x * x;
        }
        return sum;
    }


    @Override
    public double getCoord(int i) {
        return coords[i].get();
    }


    @Override
    public void div(int x) {
        for (int i = 0; i < this.dim; ++i) {
            coords[i].div(x);
        }
    }
}
