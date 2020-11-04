package br.ufrn.util;

import br.ufrn.point.*;

public class CreatePointHelper {
    CreatePointHelper(){}

    public static CreatePointInterface getInterface(String pointParameter){
        CreatePointInterface createPointInterface;
        if (pointParameter.equals("seq")) {
            createPointInterface = SequentialPoint::new;
        } else if (pointParameter.equals("par")) {
            createPointInterface = ParallelPoint::new;
        } else if (pointParameter.equals("stream")) {
            createPointInterface = StreamPoint::new;
        } else if (pointParameter.equals("exec")) {
            createPointInterface = ExecutorPoint::new;
        } else if (pointParameter.equals("fork")) {
            createPointInterface = ForkJoinPoint::new;
        } else {
            throw new RuntimeException();
        }
        return createPointInterface;
    }

}
