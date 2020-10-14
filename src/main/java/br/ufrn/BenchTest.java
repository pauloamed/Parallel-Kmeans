package br.ufrn;

import br.ufrn.point.Point;
import br.ufrn.point.SequentialPoint;
import org.openjdk.jmh.annotations.*;

import java.util.concurrent.TimeUnit;

public class BenchTest {
	@State(Scope.Thread)
	public static class state {
		public static SequentialPoint point = new SequentialPoint(new double[]{1, 1, 1});
	}


	@Benchmark
	@Fork(value = 1, warmups = 2)
	@BenchmarkMode(Mode.AverageTime)
	@Warmup(iterations = 5) 
	@OutputTimeUnit(TimeUnit.MICROSECONDS)
	public void testDistanceBetween() {
		Point.distanceBetween(state.point, state.point);
	}


	@Benchmark
	@Fork(value = 1, warmups = 2)
	@BenchmarkMode(Mode.AverageTime)
	@Warmup(iterations = 5)
	@OutputTimeUnit(TimeUnit.MICROSECONDS)
	public void testAdd() {
		state.point.add(state.point);
	}
}