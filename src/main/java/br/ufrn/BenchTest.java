package br.ufrn;

import br.ufrn.point.SequentialPoint;
import org.openjdk.jmh.annotations.*;

import java.util.concurrent.TimeUnit;

public class BenchTest {
	@Benchmark
	@Fork(value = 1, warmups = 2)
	@BenchmarkMode(Mode.AverageTime)
	@Warmup(iterations = 5) 
	@OutputTimeUnit(TimeUnit.MICROSECONDS)
	public void testAddFunc() {
		int numDim = 1;
		SequentialPoint point = new SequentialPoint(new double[]{1, 1, 1});
		for(int i = 0; i < 10000; ++i){
			point.distanceTo(point);
		}
	}
}