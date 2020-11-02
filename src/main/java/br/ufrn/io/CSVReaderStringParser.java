package br.ufrn.io;

import br.ufrn.point.Point;
import br.ufrn.util.CreatePointInterface;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.time.Instant;
import java.util.stream.Stream;

public class CSVReaderStringParser implements CSVReader{

    private String delimiter;


    public CSVReaderStringParser(String delimiter){
        this.delimiter = delimiter;

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
                String[] parsedLine = line.split(delimiter);
                int pointPos = Integer.parseInt(parsedLine[0]);
                if(pointPos != -1){
                    double[] coords = new double[dimPoints];
                    for(int i = 0; i < dimPoints; ++i){
                        coords[i] = Double.parseDouble(parsedLine[i + 1]);
                    }
                    points[pointPos] = pointInterface.createPoint(coords);
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }

        return points;
    }



}
