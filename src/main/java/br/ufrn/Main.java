package br.ufrn;

import br.ufrn.io.CSVReader;
import br.ufrn.io.CSVReaderNoString;
import br.ufrn.io.CSVReaderStringBuilder;
import br.ufrn.io.CSVReaderStringParser;
import br.ufrn.kmeans.*;
import br.ufrn.point.*;

import net.sf.saxon.expr.instruct.Fork;
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

        if(readingMech.equals("no")){
            csvReader = new CSVReaderNoString();
        }else if(readingMech.equals("builder")){
            csvReader = new CSVReaderStringBuilder();
        }else if(readingMech.equals("parser")){
            csvReader = new CSVReaderStringParser();
        }else{
            throw new Exception();
        }

        double[][] coords = csvReader.readCoords(inputFilePath, !algorithm.equals("seq"));


        Kmeans kmeans;
        Point[] points = new Point[coords.length];

        System.out.println("Starting Main. Number of points: " + points.length + "; Dim: " + points[0].getDim());

        if(algorithm.equals("seq")){
            kmeans = new SequentialKmeans();
        }else if(algorithm.equals("par")){
            kmeans = new ParallelKmeans(Runtime.getRuntime().availableProcessors());
        }else if(algorithm.equals("stream")){
            kmeans = new StreamKmeans();
        }else if(algorithm.equals("exec")){
            kmeans = new ExecutorKmeans();
        }else if(algorithm.equals("fork")){
            kmeans = new ForkJoinKmeans(3);
        }else{
            throw new RuntimeException();
        }

        if(algorithm.equals("seq")){
            for(int i = 0; i < coords.length; ++i){
                points[i] = new SequentialPoint(coords[i]);
            }
        }else if(algorithm.equals("par")){
            for(int i = 0; i < coords.length; ++i){
                points[i] = new ParallelPoint(coords[i]);
            }
        }else if(algorithm.equals("stream")){
            for(int i = 0; i < coords.length; ++i){
                points[i] = new StreamPoint(coords[i]);
            }
        }else if(algorithm.equals("exec")){
            for(int i = 0; i < coords.length; ++i){
                points[i] = new ExecutorPoint(coords[i]);
            }
        }else{
            throw new RuntimeException();
        }

        int[] classes = kmeans.run(points, K, numIters);
    }
}
