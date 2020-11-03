package br.ufrn;

import br.ufrn.io.CSVReader;
import br.ufrn.io.CSVReaderNoString;
import br.ufrn.io.CSVReaderStringBuilder;
import br.ufrn.io.CSVReaderStringParser;
import br.ufrn.kmeans.*;
import br.ufrn.point.*;
import org.apache.commons.cli.*;


public class Main {

    private static Options initCLIOptions() {
        Options options = new Options();

        Option inputOpt = new Option("i", "input", true, "Input file path");
        Option KOpt = new Option("k", true, "Number of clusters");
        Option numItersOpt = new Option("n", "iterations", true, "Number of iterations");
        Option parallelOpt = new Option("a", "algorithm", true, "Algo");
        Option readingMechOpt = new Option("r", "reading", true, "File reading mechanism");

        inputOpt.setRequired(true);
        KOpt.setRequired(true);
        numItersOpt.setRequired(true);
        parallelOpt.setRequired(true);
        readingMechOpt.setRequired(true);

        options.addOption(inputOpt);
        options.addOption(KOpt);
        options.addOption(numItersOpt);
        options.addOption(parallelOpt);
        options.addOption(readingMechOpt);

        return options;
    }


    public static void main(String[] args) throws Exception {
        Options options = initCLIOptions();

        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();
        CommandLine cmd = null;

        try {
            cmd = parser.parse(options, args);
        } catch (ParseException e) {
            System.out.println(e.getMessage());
            formatter.printHelp("utility-name", options);

            System.exit(1);
        }


        String inputFilePath = cmd.getOptionValue("input");
        int K = Integer.parseInt(cmd.getOptionValue("k"));
        String algorithm = cmd.getOptionValue("algorithm");
        int numIters = Integer.parseInt(cmd.getOptionValue("iterations"));
        String readingMech = cmd.getOptionValue("reading");

        CSVReader csvReader;

        if (readingMech.equals("no")) {
            csvReader = new CSVReaderNoString(";");
        } else if (readingMech.equals("builder")) {
            csvReader = new CSVReaderStringBuilder(";");
        } else if (readingMech.equals("parser")) {
            csvReader = new CSVReaderStringParser(";");
        } else {
            throw new Exception();
        }

        Point[] points;

        Kmeans kmeans;

        if (algorithm.equals("seq")) {
            kmeans = new SequentialKmeans();
        } else if (algorithm.equals("par")) {
            kmeans = new ParallelKmeans(Runtime.getRuntime().availableProcessors());
        } else if (algorithm.equals("stream")) {
            kmeans = new StreamKmeans();
        } else if (algorithm.equals("exec")) {
            kmeans = new ExecutorKmeans();
        } else if (algorithm.equals("fork")) {
            kmeans = new ForkJoinKmeans(5);
        } else {
            throw new RuntimeException();
        }

        if (algorithm.equals("seq")) {
            points = csvReader.readCoords(inputFilePath, false, SequentialPoint::new);
        } else if (algorithm.equals("par")) {
            points = csvReader.readCoords(inputFilePath, true, ParallelPoint::new);
        } else if (algorithm.equals("stream")) {
            points = csvReader.readCoords(inputFilePath, true, StreamPoint::new);
        } else if (algorithm.equals("exec")) {
            points = csvReader.readCoords(inputFilePath, true, ExecutorPoint::new);
        } else if (algorithm.equals("fork")) {
            points = csvReader.readCoords(inputFilePath, true, ForkJoinPoint::new);
        } else {
            throw new RuntimeException();
        }

        System.out.println("Starting. Number of points: " + points.length + "; Dim: " + points[0].getDim());
        int[] classes = kmeans.run(points, K, numIters);
        for(int i = 0; i < classes.length; ++i){
            System.out.println(classes[i]);
        }
    }
}
