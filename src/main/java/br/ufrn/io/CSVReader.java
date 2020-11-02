package br.ufrn.io;

import br.ufrn.point.Point;
import br.ufrn.util.CreatePointInterface;

import java.io.IOException;

public interface CSVReader {
    Point[] readCoords(String pathToCSV, boolean parallel, CreatePointInterface pointInterface) throws IOException;
}
