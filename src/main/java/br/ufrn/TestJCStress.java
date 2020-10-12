package br.ufrn;

import br.ufrn.point.SequentialPoint;
import org.openjdk.jcstress.annotations.*;

import java.util.concurrent.locks.ReentrantLock;


public class TestJCStress {
    private static int pointsDim = 4;

    static ReentrantLock lock = new ReentrantLock();

    @State
    public static class MyState{
        SequentialPoint p;
        MyState(){
            double coords[] = new double[pointsDim];
            for(int i = 0; i < pointsDim; ++i) coords[i] = 1;
            p = new SequentialPoint(coords);
        }
    }

    @JCStressTest
    @Description("Test of add method of Point class")
    @Outcome(id= {"(3.0, 3.0, 3.0, 3.0)"}, expect = Expect.ACCEPTABLE, desc = "Valid")
    public static class AddMethodTest {

        SequentialPoint pointToAdd = new SequentialPoint(new double[]{1.0, 1.0, 1.0, 1.0});

        @Actor
        public void actor0(MyState myState, PointResultJCStress r) {
            myState.p.add(pointToAdd);
        }

        @Actor
        public void actor1(MyState myState, PointResultJCStress r) {
            myState.p.add(pointToAdd);
        }

        @Arbiter
        public void arbiter(MyState myState, PointResultJCStress r){
            r.point = myState.p;
        }
    }

    @JCStressTest
    @Description("Test of div method of Point class")
    @Outcome(id= {"(1.0, 1.0, 1.0, 1.0)"}, expect = Expect.ACCEPTABLE, desc = "Valid")
    public static class DivMethodTest {

        int divisor = -1;

        @Actor
        public void actor0(MyState myState, PointResultJCStress r) {
            myState.p.div(divisor);
        }

        @Actor
        public void actor1(MyState myState, PointResultJCStress r) {
            myState.p.div(divisor);
        }

        @Arbiter
        public void arbiter(MyState myState, PointResultJCStress r){
            r.point = myState.p;
        }
    }
}

