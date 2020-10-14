package br.ufrn;

import java.io.Serializable;

import br.ufrn.io.CSVReaderStringBuilder;
import br.ufrn.kmeans.Kmeans;
import br.ufrn.kmeans.ParallelKmeans;
import br.ufrn.kmeans.SequentialKmeans;
import br.ufrn.point.SequentialPoint;
import org.apache.jmeter.protocol.java.sampler.AbstractJavaSamplerClient;
import org.apache.jmeter.protocol.java.sampler.JavaSamplerContext;
import org.apache.jmeter.samplers.SampleResult;
import org.apache.jmeter.config.Arguments;


public class JmeterTest extends AbstractJavaSamplerClient implements Serializable {
    private static final long serialVersionUID = 1L;

    // set up default arguments for the JMeter GUI
    @Override
    public Arguments getDefaultParameters() {
        Arguments defaultParameters = new Arguments();
        defaultParameters.addArgument("NumberOfIterations", "10");
        defaultParameters.addArgument("K", "3");
        defaultParameters.addArgument("InputFile", "input.csv");
        defaultParameters.addArgument("IsParallel", "false");
        return defaultParameters;
    }

    @Override
    public SampleResult runTest(JavaSamplerContext context) {
        // pull parameters
        String numberOfIterationsStr = context.getParameter( "NumberOfIterations" );
        String KStr = context.getParameter( "K" );
        String inputFile = context.getParameter( "InputFile" );
        String isParallelStr = context.getParameter( "IsParallel" );

        int numberOfThreads = Runtime.getRuntime().availableProcessors();
        int numberOfIterations = Integer.parseInt(numberOfIterationsStr);
        int K = Integer.parseInt(KStr);
        boolean isParallel = Boolean.parseBoolean(isParallelStr);

        SampleResult result = new SampleResult();
        result.sampleStart(); // start stopwatch

        try {
            CSVReaderStringBuilder csvReader = new CSVReaderStringBuilder(";");
            double[][] coords;
            coords = csvReader.readCoords(inputFile, isParallel);

            SequentialPoint seqPoints[] = new SequentialPoint[coords.length];
            for(int i = 0; i < coords.length; ++i){
                seqPoints[i] = new SequentialPoint(coords[i]);
            }

            System.out.println("Starting Main. Number of points: " + seqPoints.length + "; Dim: " + seqPoints[0].getDim());

            Kmeans kmeans;
            if(isParallel) kmeans = new ParallelKmeans(numberOfThreads);
            else kmeans = new SequentialKmeans();

            kmeans.run(seqPoints, K, numberOfIterations);


            result.sampleEnd(); // stop stopwatch
            result.setSuccessful( true );
            result.setResponseMessage( "Successfully performed action" );
            result.setResponseCodeOK(); // 200 code
        } catch (Exception e) {
            result.sampleEnd(); // stop stopwatch
            result.setSuccessful( false );
            result.setResponseMessage( "Exception: " + e );

            // get stack trace as a String to return as document data
            java.io.StringWriter stringWriter = new java.io.StringWriter();
            e.printStackTrace( new java.io.PrintWriter( stringWriter ) );
            result.setResponseData( stringWriter.toString() );
            result.setDataType( org.apache.jmeter.samplers.SampleResult.TEXT );
            result.setResponseCode( "500" );
        }

        return result;
    }
}