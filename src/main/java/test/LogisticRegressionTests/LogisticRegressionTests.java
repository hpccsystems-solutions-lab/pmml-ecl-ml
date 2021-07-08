package test.LogisticRegressionTests;

import com.hpccsystems.pmml2ecl.ECLConverter;
import com.hpccsystems.pmml2ecl.PMMLConverter;
import org.junit.Test;
import test.CommonTestOperations;

import java.io.File;
import java.util.ArrayList;
import java.util.Random;

import static org.junit.Assert.*;

public class LogisticRegressionTests {

    static String testDirectory = System.getProperty("user.dir") + "/src/main/java/test/LogisticRegressionTests/output";
    static String outputDirectory = System.getProperty("user.dir") + "/output";

    @Test
    public void singleLogisticRegression() throws Exception {

        //TODO: For some reason the betas are not accurately transferred to the ECL. Can be variations in the
        //      calculations of the models and values.
        Double[] betas = {-10.0, 20.0};

        int numPoints = 10;

        ArrayList<Double[]> listOfCoefficients = new ArrayList<>();
        listOfCoefficients.add(betas);
        String file = getFileFromArrayofCoefficients(listOfCoefficients, numPoints);

        CommonTestOperations.writeToFile(testDirectory + "/SingleLogisticRegressionTest.ecl", file);

        new ECLConverter(testDirectory + "/SingleLogisticRegressionTest.ecl");
        new PMMLConverter(outputDirectory + "/PMMLOutput-LogisticRegression1.xml");
    }

    @Test
    public void multipleLogisticRegression() throws Exception {
        
    }

    private Double[] fillCoefficients(int numCoef) {
        Random random = new Random();
        Double[] coef = new Double[numCoef];
        for (int i = 0; i < coef.length; i++) {
            coef[i] = random.nextDouble() * 50 * (random.nextDouble() > .5 ? -1 : 1);
        }
        return coef;
    }

    private void printCoefficients(Double[] coefficients) {
        for (int i = 0; i < coefficients.length; i++) {
            System.out.println("x" + (i + 1) + ": " + coefficients[i]);
        }
    }

    private String getFileFromArrayofCoefficients(ArrayList<Double[]> betas, int numPoints) throws Exception {
        Random random = new Random();

        String file = CommonTestOperations.getFileContents(testDirectory + "/LogisticRegressionTemplate.ecl");
        ArrayList<String> finalDeps = new ArrayList<>();
        ArrayList<String> finalIndeps = new ArrayList<>();
        for (int wi = 1; wi < betas.size() + 1; wi++) {
            Double[] betasToWorkOn = betas.get(wi - 1);
            ArrayList<String> depStrings = new ArrayList<>();
            ArrayList<String> indepStrings = new ArrayList<>();

            for (int i = 0; i < numPoints; i++) {

                double randomValue = Math.random();
                Double[] values = new Double[]{randomValue};
                //Fill up with random values.
                int predictedValue = getPredictedValue(betasToWorkOn, values);

                depStrings.add("{" + wi + ", " + (i + 1) + ", 1, " + randomValue + "}");
                indepStrings.add("{" + wi + ", " + (i + 1) + ", 1, " + predictedValue + "}");

            }
            finalDeps.add(String.join(",\n    ", depStrings));
            finalIndeps.add(String.join(",\n    ", indepStrings));
        }

        file = file.replaceFirst("classificationsREPLACE", String.join(",\n    ", finalDeps));
        file = file.replaceFirst("observationsREPLACE", String.join(",\n    ", finalIndeps));
        return file;
    }

    private int getPredictedValue(Double[] coefficients, Double[] values) {
        double totalExp = coefficients[0];
        for (int i = 1; i < coefficients.length; i++) {
            totalExp += coefficients[i] * values[i - 1];
        }
        return 1 / (1 + Math.exp(-totalExp)) >= .5 ? 1 : 0;
    }

    @Test
    public void testPredictedValues() {
        Double[] coefficients = new Double[]{-10.0, 20.0};

        for (int i = 0; i < 50; i++) {
            double random = Math.random();
            Double[] value = new Double[]{random};
            int predict = getPredictedValue(coefficients, value);
            assertTrue((random >= .5 && predict == 1) || (random < .5 && predict == 0));
            System.out.println(String.format("%.2f | %d", random, predict));
        }
    }

    @Test
    public void clearOutputFolder() {
        int counter = 1;
        File pmmlFile;
        do {
            pmmlFile = new File(outputDirectory + "/PMMLOutput-LogisticRegression" + counter + ".xml");
            counter++;
        } while (pmmlFile.delete());
        assertFalse(new File(outputDirectory + "/PMMLOutput-LogisticRegression1.xml").exists());
        counter = 1;
        File eclFile;
        do {
            eclFile = new File(outputDirectory + "/ECLOutput" + counter + ".ecl");
            counter++;
        } while (eclFile.delete());
        assertFalse(new File(outputDirectory + "/ECLOutput1.ecl").exists());
    }

}
