package br.ufrn;

import br.ufrn.io.CSVReader;
import br.ufrn.io.CSVReaderNoString;
import br.ufrn.io.CSVReaderStringBuilder;
import br.ufrn.io.CSVReaderStringParser;
import br.ufrn.kmeans.Kmeans;
import br.ufrn.kmeans.ParallelKmeans;
import br.ufrn.kmeans.SequentialKmeans;
import br.ufrn.point.SequentialPoint;

import org.apache.commons.cli.Options;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;


public class Main{

    private static Options initCLIOptions(){
        Options options = new Options();

        Option inputOpt = new Option("i", "input", true, "Input file path");
        Option KOpt = new Option("k", true, "Number of clusters");
        Option numItersOpt = new Option("n", "iterations", true, "Number of iterations");
        Option parallelOpt = new Option("p", "parallel", true, "Parallel execution");
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
        boolean parallel = Boolean.parseBoolean(cmd.getOptionValue("parallel"));
        int numIters = Integer.parseInt(cmd.getOptionValue("iterations"));
        String readingMech = cmd.getOptionValue("reading");

        CSVReader csvReader;

        if(readingMech.equals("no")){
            csvReader = new CSVReaderNoString();
        }else if(readingMech.equals("builder")){
            csvReader = new CSVReaderStringBuilder();
        }else if(readingMech.equals("parser")){
            csvReader = new CSVReaderStringParser();
        }else{
            throw new Exception();
        }

        double[][] coords = csvReader.readCoords(inputFilePath, parallel);


        SequentialPoint[] seqPoints = new SequentialPoint[coords.length];
        for(int i = 0; i < coords.length; ++i){
            seqPoints[i] = new SequentialPoint(coords[i]);
        }

        System.out.println("Starting Main. Number of points: " + seqPoints.length + "; Dim: " + seqPoints[0].getDim());

        Kmeans kmeans;
        if(parallel){
            kmeans = new ParallelKmeans(Runtime.getRuntime().availableProcessors());
        }else{
            kmeans = new SequentialKmeans();
        }
        int[] classes = kmeans.run(seqPoints, K, numIters);
    }
}
