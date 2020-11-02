package br.ufrn.io;


import br.ufrn.point.Point;
import br.ufrn.util.CreatePointInterface;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.stream.Stream;

public class CSVReaderNoString implements CSVReader {

    private String delimiter;

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

    public Point[] readCoords(String pathToCSV, boolean parallel, CreatePointInterface pointInterface) throws IOException {
        FileReader csvFileReader = new FileReader(pathToCSV);
        BufferedReader headerReader = new BufferedReader(csvFileReader);

        String header = headerReader.readLine();

        csvFileReader.close();
        headerReader.close();

        String[] headerSplitted = header.split(this.delimiter);

        int numPoints = Integer.parseInt(headerSplitted[1]);
        int dimPoints = Integer.parseInt(headerSplitted[2]);

        Point[] points = new Point[numPoints];

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
                        }else{
                            pointCoords[delCount - 1] = readDouble(line, lastPos + 1, i);
                        }
                        delCount++;
                        lastPos = i;
                    }
                }
                if(pointPos != -1){
                    points[pointPos] = pointInterface.createPoint(pointCoords);
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }

        return points;
    }



}
