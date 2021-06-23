package test.LinearRegressionTests;

import org.junit.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.Random;

public class LogisticRegressionTests {

    @Test
    public static void runDeterminedSingleLinearRegression(double x0, double x1) {
        File outputDir = new File(System.getProperty("user.dir") + "/output/test");
        outputDir.mkdirs();

    }

    /**
     * Uses random values and multiplies them by the coefficients to get a predicted value.
     * [0 - n-2] are the values used. [n-1] is the predicted value.
     * @param xCoefficients x0 (intercept) to xN
     * @return x1 to xN and xN + 1 (calculated value)
     */
    public static double[] createRandomDependent(double[] xCoefficients) {
        double dependent = xCoefficients[0];
        double[] values = new double[xCoefficients.length];
        Random rand = new Random();
        for (int i = 1; i < xCoefficients.length; i++) {
            double multiplier = rand.nextDouble();
            values[i - 1] = multiplier;
            dependent += multiplier * xCoefficients[i];
        }
        values[values.length - 1] = dependent;
        return values;
    }


}
