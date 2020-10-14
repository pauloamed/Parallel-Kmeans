package br.ufrn.io;


import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.stream.Stream;

public class CSVReaderNoString implements CSVReader {

    private String delimiter;

    public CSVReaderNoString(){
        this.delimiter = ";";
    }


    public CSVReaderNoString(String delimiter){
        this.delimiter = delimiter;
    }

    private int readInt(String s, int start, int end){
        boolean isNeg = false;
        if(s.charAt(start) == '-'){
            isNeg = true;
            start++;
        }
        int num = 0;
        for(int i = start; i < end; ++i){
            num *= 10;
            num += Character.getNumericValue(s.charAt(i));
        }
        if(isNeg) num = -num;
        return num;
    }

    private double readDouble(String s, int start, int end){
        boolean isNeg = false;
        if(s.charAt(start) == '-'){
            isNeg = true;
            start++;
        }
        double num = 0;
        boolean afterPoint = false;
        int divisor = 1;
        for(int i = start; i < end; ++i){
            if(s.charAt(i) == this.delimiter.charAt(0)){
                afterPoint = true;
            }else{
                num *= 10;
                num += Character.getNumericValue(s.charAt(i));

                if(afterPoint){
                    divisor *= 10;
                }
            }
        }
        if(isNeg) num = -num;
        return num / divisor;
    }

    public double[][] readCoords(String pathToCSV, boolean parallel) throws IOException {
        FileReader csvFileReader = new FileReader(pathToCSV);
        BufferedReader headerReader = new BufferedReader(csvFileReader);

        String header = headerReader.readLine();

        csvFileReader.close();
        headerReader.close();

        String[] headerSplitted = header.split(this.delimiter);

        int numPoints = Integer.parseInt(headerSplitted[1]);
        int dimPoints = Integer.parseInt(headerSplitted[2]);

        double[][] coords = new double[numPoints][];

        File csvFile = new File(pathToCSV);

        try{
            Stream<String> lineStream = Files.lines(csvFile.toPath(), StandardCharsets.UTF_8);

            if(parallel){
                lineStream = lineStream.parallel();
            }

            lineStream.forEach(line -> {
                int pointPos = -1;
                int delCount = 0;
                int lastPos = -1;
                double[] pointCoords = new double[dimPoints];
                for(int i = 0; i < line.length(); ++i){
                    if(line.charAt(i) == this.delimiter.charAt(0)){
                        if(delCount == 0){
                            pointPos = readInt(line, lastPos + 1, i);
                            if(pointPos == -1) return;
                        }else{
                            pointCoords[delCount - 1] = readDouble(line, lastPos + 1, i);
                        }
                        delCount++;
                        lastPos = i;
                    }
                }
                coords[pointPos] = pointCoords;
            });
        } catch (IOException e) {
            e.printStackTrace();
        }

        return coords;
    }



}
