package br.ufrn.io;

import java.io.IOException;

public interface CSVReader {
    double[][] readCoords(String pathToCSV, boolean parallel) throws IOException;
}
