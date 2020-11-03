package br.ufrn.point;

import br.ufrn.util.AtomicDouble;
import br.ufrn.util.PairDoubleInt;
import br.ufrn.util.Task;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;
import java.util.concurrent.RecursiveTask;

public class ForkJoinPoint extends Point {

    private static final int THRESHOLD = 4;

    private final AtomicDouble[] coords;
    private final ForkJoinPool pool;

    public ForkJoinPoint(int dim) {
        this.coords = new AtomicDouble[dim];
        for (int i = 0; i < coords.length; ++i) {
            this.coords[i] = new AtomicDouble(0);
        }
        this.dim = this.coords.length;
        this.pool = ForkJoinPool.commonPool();
    }

    public ForkJoinPoint(double[] coords) {
        this.coords = new AtomicDouble[coords.length];
        for (int i = 0; i < coords.length; ++i) {
            this.coords[i] = new AtomicDouble(coords[i]);
        }
        this.dim = this.coords.length;
        this.pool = ForkJoinPool.commonPool();
    }


    @Override
    public int closestTo(Point[] points) {
        ClosestToForkJoin task = new ClosestToForkJoin(0, points.length, points);
        return pool.invoke(task).y;
    }


    @Override
    public void add(Point p) {
        AddTask localTask = new AddTask(p);
        ActionForkJoin task = new ActionForkJoin(0, dim, coords, localTask);
        pool.invoke(task);
    }


    @Override
    public double distanceTo(Point p) {
        SumForkJoin task = new SumForkJoin(0, dim, coords, p);
        return pool.invoke(task);
    }

    @Override
    public double getCoord(int i) {
        return coords[i].get();
    }


    @Override
    public void div(int x) {
        DivTask localTask = new DivTask(x);
        ActionForkJoin task = new ActionForkJoin(0, dim, coords, localTask);
        pool.invoke(task);
    }


    private class ClosestToForkJoin extends RecursiveTask<PairDoubleInt> {

        Point[] points;
        int l, r;

        ClosestToForkJoin(int l, int r, Point[] points) {
            this.l = l;
            this.r = r;
            this.points = points;
        }

        @Override
        protected PairDoubleInt compute() {
            return _compute(l, r);
        }

        private PairDoubleInt _compute(int l, int r) {
            int size = r - l;
            if (size == 1) {
                double distanceToPoint = distanceTo(points[l]);
                return new PairDoubleInt(distanceToPoint, l);
            } else {
                int mid = l + size / 2;
                ClosestToForkJoin firstSubtask = new ClosestToForkJoin(l, mid, points);
                ClosestToForkJoin secondSubtask = new ClosestToForkJoin(mid, r, points);
                secondSubtask.fork();
                return PairDoubleInt.merge(firstSubtask.compute(), secondSubtask.join());
            }
        }
    }


    private class SumForkJoin extends RecursiveTask<Double> {

        Point p;
        int l, r;
        AtomicDouble[] coords;

        SumForkJoin(int l, int r, AtomicDouble[] coords, Point p) {
            this.l = l;
            this.r = r;
            this.coords = coords;
            this.p = p;
        }

        @Override
        protected Double compute() {
            return _compute(l, r);
        }

        private Double _compute(int l, int r) {
            int size = r - l;
            if (size <= THRESHOLD) {
                Double sum = 0.0;
                for (int i = l; i < r; ++i) {
                    double x = (getCoord(i) - p.getCoord(i));
                    sum += x * x;
                }
                return sum;
            } else {
                int mid = l + size / 2;
                SumForkJoin firstSubtask = new SumForkJoin(l, mid, coords, p);
                SumForkJoin secondSubtask = new SumForkJoin(mid, r, coords, p);
                secondSubtask.fork();
                return firstSubtask.compute() + secondSubtask.join();
            }
        }
    }


    private class ActionForkJoin extends RecursiveAction {

        Task task;
        int l, r;
        AtomicDouble[] coords;

        ActionForkJoin(int l, int r, AtomicDouble[] coords, Task task) {
            this.l = l;
            this.r = r;
            this.coords = coords;
            this.task = task;
        }

        @Override
        protected void compute() {
            _compute(l, r);
        }

        private void _compute(int l, int r) {
            int size = r - l;
            if (size <= THRESHOLD) {
                task.execute(l, r);
            } else {
                int mid = l + size / 2;
                ActionForkJoin firstSubtask = new ActionForkJoin(l, mid, coords, task);
                ActionForkJoin secondSubtask = new ActionForkJoin(mid, r, coords, task);

                invokeAll(firstSubtask, secondSubtask);
            }
        }
    }


    private class DivTask implements Task {
        int x;

        DivTask(int x) {
            this.x = x;
        }

        public void execute(int l, int r) {
            for (int i = l; i < r; ++i) {
                coords[i].div(x);
            }
        }
    }


    private class AddTask implements Task {
        Point p;

        AddTask(Point p) {
            this.p = p;
        }

        public void execute(int l, int r) {
            for (int i = l; i < r; ++i) {
                coords[i].addAndGet(p.getCoord(i));
            }
        }
    }
}
