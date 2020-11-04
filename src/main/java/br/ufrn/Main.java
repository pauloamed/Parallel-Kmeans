package br.ufrn;

import br.ufrn.io.CSVReader;
import br.ufrn.io.CSVReaderNoString;
import br.ufrn.io.CSVReaderStringBuilder;
import br.ufrn.io.CSVReaderStringParser;
import br.ufrn.kmeans.*;
import br.ufrn.point.*;
import br.ufrn.util.InitHelper;
import br.ufrn.util.CreatePointInterface;
import org.apache.commons.cli.*;


public class Main {

    private static Options initCLIOptions() {
        Options options = new Options();

        Option inputOpt = new Option("i", "input", true, "Input file path");
        Option KOpt = new Option("k", true, "Number of clusters");
        Option numItersOpt = new Option("n", "iterations", true, "Number of iterations");
        Option readingMechOpt = new Option("r", "reading", true, "File reading mechanism");
        Option algorithmOpt = new Option("a", "algorithm", true, "Algorithm execution mode");
        Option pointOpt = new Option("p", "point", true, "Point execution mode");


        inputOpt.setRequired(true);
        KOpt.setRequired(true);
        numItersOpt.setRequired(true);
        algorithmOpt.setRequired(true);
        pointOpt.setRequired(true);
        readingMechOpt.setRequired(true);

        options.addOption(inputOpt);
        options.addOption(KOpt);
        options.addOption(numItersOpt);
        options.addOption(algorithmOpt);
        options.addOption(pointOpt);
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
        String point = cmd.getOptionValue("point");
        int numIters = Integer.parseInt(cmd.getOptionValue("iterations"));
        String readingMech = cmd.getOptionValue("reading");

        CSVReader csvReader = InitHelper.getReader(readingMech);
        CreatePointInterface createPointInterface = InitHelper.getInterface(point);
        boolean isParallel = !(algorithm.equals("seq"));
        Point[] points = csvReader.readCoords(inputFilePath, isParallel, createPointInterface);
        Kmeans kmeans = InitHelper.getKmeans(algorithm, createPointInterface);

        System.out.println("Starting. Number of points: " + points.length + "; Dim: " + points[0].getDim());
        int[] classes = kmeans.run(points, K, numIters);
        for(int i = 0; i < classes.length; ++i){
            System.out.println(classes[i]);
        }
    }
}
