package br.ufrn;

import br.ufrn.io.CSVReader;
import br.ufrn.io.CSVReaderStringBuilder;
import br.ufrn.kmeans.Kmeans;
import br.ufrn.kmeans.ParallelKmeans;
import br.ufrn.kmeans.SequentialKmeans;
import br.ufrn.point.Point;
import br.ufrn.point.SequentialPoint;
import br.ufrn.util.CreatePointInterface;
import br.ufrn.util.InitHelper;
import org.apache.jmeter.config.Arguments;
import org.apache.jmeter.protocol.java.sampler.AbstractJavaSamplerClient;
import org.apache.jmeter.protocol.java.sampler.JavaSamplerContext;
import org.apache.jmeter.samplers.SampleResult;

import java.io.IOException;
import java.io.Serializable;


public class JmeterTest extends AbstractJavaSamplerClient implements Serializable {
    private static final long serialVersionUID = 1L;

    // set up default arguments for the JMeter GUI
    @Override
    public Arguments getDefaultParameters() {
        Arguments defaultParameters = new Arguments();
        defaultParameters.addArgument("NumberOfIterations", "10");
        defaultParameters.addArgument("K", "3");
        defaultParameters.addArgument("InputFile", "input.csv");
        defaultParameters.addArgument("reading", "no");
        defaultParameters.addArgument("point", "seq");
        defaultParameters.addArgument("algorithm", "seq");
        return defaultParameters;
    }

    @Override
    public SampleResult runTest(JavaSamplerContext context) {
        // pull parameters
        String numberOfIterationsStr = context.getParameter("NumberOfIterations");
        String KStr = context.getParameter("K");
        String inputFile = context.getParameter("InputFile");
        String readingMech = context.getParameter("reading");
        String point = context.getParameter("point");
        String algorithm = context.getParameter("algorithm");



        int numberOfIterations = Integer.parseInt(numberOfIterationsStr);
        int K = Integer.parseInt(KStr);

        SampleResult result = new SampleResult();
        result.sampleStart(); // start stopwatch

        try {
            CSVReader csvReader = InitHelper.getReader(readingMech);
            CreatePointInterface createPointInterface = InitHelper.getInterface(point);
            boolean isParallel = !(algorithm.equals("seq"));
            Point[] points = csvReader.readCoords(inputFile, isParallel, createPointInterface);
            Kmeans kmeans = InitHelper.getKmeans(algorithm, createPointInterface);

            System.out.println("Starting Main. Number of points: " + points.length + "; Dim: " + points[0].getDim());

            kmeans.run(points, K, numberOfIterations);


            result.sampleEnd(); // stop stopwatch
            result.setSuccessful(true);
            result.setResponseMessage("Successfully performed action");
            result.setResponseCodeOK(); // 200 code
        } catch (Exception e) {
            result.sampleEnd(); // stop stopwatch
            result.setSuccessful(false);
            result.setResponseMessage("Exception: " + e);

            // get stack trace as a String to return as document data
            java.io.StringWriter stringWriter = new java.io.StringWriter();
            e.printStackTrace(new java.io.PrintWriter(stringWriter));
            result.setResponseData(stringWriter.toString());
            result.setDataType(org.apache.jmeter.samplers.SampleResult.TEXT);
            result.setResponseCode("500");
        }

        return result;
    }
}