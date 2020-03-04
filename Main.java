class Main{

    public double[] convertLineToCoords(String[] line){
        double[] coords = new double[line.length];

        for(int i = 0; i < line.length; ++i){
            coords[i] = Double.parseDouble(line[i]);
        }

        return coords;
    }

    public static void main(String[] args){
        CSVReader csvReader = new CSVReader();
        csvReader.setReader(pathToCSV);


        ArrayList<Point> points = new ArrayList<Point>;

        String[] line;
        while(csvReader.getNextLine(line)){
            double[] coords = convertLineToCoords(line);
            points.add(new Point(coords));
        }


        Kmeans kmeansAlgo = new Kmeans();
        int classes[] = kmeansAlgo.run(points, 3);

        for(int i = 0; i < classes.length; ++i){
            System.out.println("Point " + i + ", class: " + classes[i]);
        }
    }
}
