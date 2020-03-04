class CSVReader{

    private BufferedReader csvReader;
    private String delimiter;

    CSVReader(){
        this.delimiter = ",";
    }


    CSVReader(String delimiter){
        CSVReader();
        this.delimiter = delimiter;
    }

    public void setReader(String pathToCSV){
        FileReader csvFile = new FileReader(pathToCSV);

        if(!csvFile.isFile()){
            throw RuntimeException();
        }

        this.csvReader = new BufferedReader(csvFile);
    }


    public boolean getNextLine(String[] line){
        String row = csvReader.readLine();
        if(row != null){
            line = row.split(this.delimiter);
            return true;
        }else{
            return false;
        }
    }

}
