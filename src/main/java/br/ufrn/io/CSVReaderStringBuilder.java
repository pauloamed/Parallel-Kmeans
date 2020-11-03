package br.ufrn.io;

import br.ufrn.point.Point;
import br.ufrn.util.CreatePointInterface;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.stream.Stream;

public class CSVReaderStringBuilder implements CSVReader {

    private final String delimiter;

    public CSVReaderStringBuilder(String delimiter) {
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

        try {
            Stream<String> lineStream = Files.lines(csvFile.toPath(), StandardCharsets.UTF_8);

            if (parallel) {
                lineStream = lineStream.parallel();
            }

            lineStream.forEach(line -> {
                int pointPos = 0;
                double[] pointCoords = new double[dimPoints];

                int delCount = 0;
                StringBuilder stringBuilder = new StringBuilder();
                for (int charPos = 0; charPos < line.length(); ++charPos) {
                    if (line.charAt(charPos) == delimiter.charAt(0)) {
                        if (delCount == 0) {
                            pointPos = Integer.parseInt(stringBuilder.toString());
                        } else {
                            pointCoords[delCount - 1] = Double.parseDouble((stringBuilder.toString()));
                        }
                        stringBuilder.setLength(0);
                        delCount++;
                    } else {
                        stringBuilder.append(line.charAt(charPos));
                    }
                }
                if (pointPos != -1) {
                    points[pointPos] = pointInterface.createPoint(pointCoords);
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }

        return points;
    }


}
