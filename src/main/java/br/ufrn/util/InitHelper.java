package br.ufrn.util;

import br.ufrn.io.CSVReader;
import br.ufrn.io.CSVReaderNoString;
import br.ufrn.io.CSVReaderStringBuilder;
import br.ufrn.io.CSVReaderStringParser;
import br.ufrn.kmeans.*;
import br.ufrn.point.*;

public class InitHelper {
    private static final int FORK_JOIN_THRESHOLD = 5;

    InitHelper(){}

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

    public static CSVReader getReader(String readerParameter){
        CSVReader csvReader;

        if (readerParameter.equals("no")) {
            csvReader = new CSVReaderNoString(";");
        } else if (readerParameter.equals("builder")) {
            csvReader = new CSVReaderStringBuilder(";");
        } else if (readerParameter.equals("parser")) {
            csvReader = new CSVReaderStringParser(";");
        } else {
            throw new RuntimeException();
        }
        return csvReader;
    }


    public static Kmeans getKmeans(String algorithmParameter, CreatePointInterface createPointInterface){
        Kmeans kmeans;

        if (algorithmParameter.equals("seq")) {
            kmeans = new SequentialKmeans(createPointInterface);
        } else if (algorithmParameter.equals("par")) {
            kmeans = new ParallelKmeans(createPointInterface, Runtime.getRuntime().availableProcessors());
        } else if (algorithmParameter.equals("stream")) {
            kmeans = new StreamKmeans(createPointInterface);
        } else if (algorithmParameter.equals("exec")) {
            kmeans = new ExecutorKmeans(createPointInterface);
        } else if (algorithmParameter.equals("fork")) {
            kmeans = new ForkJoinKmeans(createPointInterface, FORK_JOIN_THRESHOLD);
        } else {
            throw new RuntimeException();
        }
        return kmeans;
    }

}
