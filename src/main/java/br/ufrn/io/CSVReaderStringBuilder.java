package br.ufrn.io;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.time.Instant;
import java.util.stream.Stream;

public class CSVReaderStringBuilder implements CSVReader{

    private String delimiter;

    public CSVReaderStringBuilder(){
        this.delimiter = ";";
    }


    public CSVReaderStringBuilder(String delimiter){
        this.delimiter = delimiter;
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
                int pointPos = 0;
                double[] pointCoords = new double[dimPoints];

                int delCount = 0;
                StringBuilder stringBuilder = new StringBuilder();
                for(int charPos = 0; charPos < line.length(); ++charPos){
                    if(line.charAt(charPos) == delimiter.charAt(0)){
                        if(delCount == 0){
                            pointPos = Integer.parseInt(stringBuilder.toString());
                            if(pointPos == -1) return;
                        }else{
                            pointCoords[delCount - 1] = Double.parseDouble((stringBuilder.toString()));
                        }
                        stringBuilder.setLength(0);
                        delCount++;
                    }else{
                        stringBuilder.append(line.charAt(charPos));
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
