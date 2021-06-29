package test.LinearRegressionTests;

import com.hpccsystems.pmml2ecl.ECLConverter;
import com.hpccsystems.pmml2ecl.PMMLConverter;
import com.hpccsystems.pmml2ecl.ecl.ECLCompiler;
import com.hpccsystems.pmml2ecl.ecl.ECLParser;
import org.junit.Test;
import test.CommonTestOperations;

import java.io.File;
import java.util.ArrayList;
import java.util.Random;

import static org.junit.Assert.*;

public class LogisticRegressionTests {

    static String testDirectory = System.getProperty("user.dir") + "/src/main/java/test/LinearRegressionTests/output";
    static String outputDirectory = System.getProperty("user.dir") + "/output";

    @Test
    public void runDeterminedSingleLinearRegression() throws Exception {
        ArrayList<Double> dependents;
        ArrayList<Double> independents;

        dependents = new ArrayList<>();
        independents = new ArrayList<>();

        Random rand = new Random();

        //Change these for different lines.
        double x0 = 5;
        double x1 = 10;
        int numPoints = 100;

        for (int i = 0; i < 100; i++) {
            double randDouble = rand.nextDouble();
            dependents.add(randDouble);
            independents.add((x1 * randDouble) + x0);
        }

        File outputDir = new File(testDirectory);
        outputDir.mkdirs();

        String file = CommonTestOperations.getFileContents(testDirectory + "/LinearRegressionTemplate.ecl");

        assertEquals(dependents.size(), independents.size());

        ArrayList<String> depStrings = new ArrayList<>();
        ArrayList<String> indepStrings = new ArrayList<>();
        for (int i = 0; i < dependents.size(); i++ ) {
            depStrings.add("{1, " + (i + 1) + ", 1, " + dependents.get(i) + "}");
            indepStrings.add("{1, " + (i + 1) + ", 1, " + independents.get(i) + "}");
        }
        String finalDep = String.join(",\n     ", depStrings);
        String finalIndep = String.join(",\n     ", indepStrings);

        file = file.replaceFirst("dependentREPLACE", finalDep);
        file = file.replaceFirst("independentREPLACE", finalIndep);

        CommonTestOperations.writeToFile(testDirectory + "/SingleLinearRegressionTest.ecl", file);

        new ECLConverter(testDirectory + "/SingleLinearRegressionTest.ecl");
        new PMMLConverter(outputDirectory + "/PMMLOutput-LinearRegression1.xml");

        //Do inspection of files to see if the values match up.
    }

    @Test
    public void multipleDeterminedLinearRegression() throws Exception {
        Double[] xCoefficients = {4.0, 9.0, 2.4, 12.0, -7.3}; //0th index is the intercept.
        int numPoints = 100;

        ArrayList<Double[]> listOfCoefficients = new ArrayList<>();
        listOfCoefficients.add(xCoefficients);
        String file = getFileFromArrayofCoefficients(listOfCoefficients, numPoints);

        CommonTestOperations.writeToFile(testDirectory + "/MultipleLinearRegressionTest.ecl", file);

        new ECLConverter(testDirectory + "/MultipleLinearRegressionTest.ecl");
        for (int i = 1; i < listOfCoefficients.size() + 1; i++) {
            new PMMLConverter(outputDirectory + "/PMMLOutput-LinearRegression" + i + ".xml", Integer.toString(i));
        }
    }

    @Test
    public void multipleWorkItemDeterminedLinearRegression() throws Exception {
        int numPoints = 50;

        ArrayList<Double[]> listOfCoefficients = new ArrayList<>();
        listOfCoefficients.add(new Double[]{.5, 9.0, 14.2});
        listOfCoefficients.add(new Double[]{2.0, -16.0, 1.2});
        listOfCoefficients.add(new Double[]{45.2, -2.1502, -.07312});
        String file = getFileFromArrayofCoefficients(listOfCoefficients, numPoints);

        CommonTestOperations.writeToFile(testDirectory + "/MultipleWILinearRegressionTest.ecl", file);

        new ECLConverter(testDirectory + "/MultipleWILinearRegressionTest.ecl");
        for (int i = 1; i < listOfCoefficients.size() + 1; i++) {
            new PMMLConverter(outputDirectory + "/PMMLOutput-LinearRegression" + i + ".xml", Integer.toString(i));
        }
    }

    @Test
    public void multipleWorkItemsUndetermined() throws Exception {
        Random random = new Random();

        int numPoints = random.nextInt(100) + 1;

        int numArrays = random.nextInt(10) + 1;

        ArrayList<Double[]> coefficients = new ArrayList<>();
        for (int i = 0; i < numArrays; i++) {
            int numCoefficients = random.nextInt(9) + 2; //Up to 10, no less than 2
            Double[] thisCoef = new Double[numCoefficients];
            for (int j = 0; j < numCoefficients; j++) {
                thisCoef[j] = random.nextDouble() * 100 * (random.nextDouble() > .5 ? -1 : 1);
            }
            coefficients.add(thisCoef);
            System.out.println("------ Work item number " + (i + 1) + " ------");
            printCoefficients(thisCoef);
        }

        String file = getFileFromArrayofCoefficients(coefficients, numPoints);

        CommonTestOperations.writeToFile(testDirectory + "/UndeterminedMultipleWILinearRegressionTest.ecl", file);

        new ECLConverter(testDirectory + "/UndeterminedMultipleWILinearRegressionTest.ecl");
        for (int i = 0; i < numArrays; i++) {
            new PMMLConverter(outputDirectory + "/PMMLOutput-LinearRegression" + (i + 1) + ".xml", Integer.toString(i + 1));
        }
        //At this point, you must check it manually using the output in the terminal and the files.
    }

    /*
    THIS TEST WILL CREATE A BUNCH OF FILES!
    U HAVE BEEN WARNED. IT SHOULDN'T CRASH UR COMPUTER THO LOL.
    RUN clearOutputFolder() TEST AFTER TO CLEAR YOUR ENVIRONMENT.
     */
    @Test
    public void linearRegressionPerformance() throws Exception {
        //I want to note that a lot of it comes from ECL Compile which is why I am doing this in parts.
        Random random = new Random();
        int[] wiIntervals = {10, 50, 100};
        int[] coefIntervals = {5, 8, 10};


        for (int wiInterval : wiIntervals) {
            for (int coefNum : coefIntervals) {
                ArrayList<Double[]> coefficients = new ArrayList<>();
                for (int i = 0; i < wiInterval; i++) {
                    coefficients.add(fillCoefficients(coefNum));
                }
                String file = getFileFromArrayofCoefficients(coefficients, 10);
                /*
                Keeping points small because we are not testing the ML Library. Don't care what happens until we receive
                the compiled ECL, so not timing that.
                 */
                CommonTestOperations.writeToFile(testDirectory + "/LinearRegressionSpeedTest.ecl", file);
                new ECLCompiler(testDirectory + "/LinearRegressionSpeedTest.ecl");
                //THIS IS WHERE THE FUN BEGINS.
                try {
                    long startTime = System.currentTimeMillis();
                    new ECLParser(System.getProperty("user.dir") + "/obj/CompileResult.xml");
                    long endTime = System.currentTimeMillis();
                    long duration = (endTime - startTime);
                    System.out.println("For " + wiInterval + " workItems and " + coefNum + " coefficients" +
                            " the Parser took " + duration + "ms");
                } catch (Exception e) {
                    System.out.println(wiInterval + " workItems and " + coefNum + " coefficients had an error.");
                }

            }
        }
    }

    @Test
    public void clearOutputFolder() {
        int counter = 1;
        File pmmlFile;
        do {
            pmmlFile = new File(outputDirectory + "/PMMLOutput-LinearRegression" + counter + ".xml");
            counter++;
        } while (pmmlFile.delete());
        assertFalse(new File(outputDirectory + "/PMMLOutput-LinearRegression1.xml").exists());
        counter = 1;
        File eclFile;
        do {
            eclFile = new File(outputDirectory + "/ECLOutput" + counter + ".ecl");
            counter++;
        } while (eclFile.delete());
        assertFalse(new File(outputDirectory + "/ECLOutput1.ecl").exists());
    }

    private Double[] fillCoefficients(int numCoef) {
        Random random = new Random();
        Double[] coef = new Double[numCoef];
        for (int i = 0; i < coef.length; i++) {
            coef[i] = random.nextDouble() * 100 * (random.nextDouble() > .5 ? -1 : 1);
        }
        return coef;
    }

    private void printCoefficients(Double[] coefficients) {
        for (int i = 0; i < coefficients.length; i++) {
            System.out.println("x" + (i + 1) + ": " + coefficients[i]);
        }
    }

    private String getFileFromArrayofCoefficients(ArrayList<Double[]> xCoefficients, int numPoints) throws Exception {
        Random random = new Random();

        String file = CommonTestOperations.getFileContents(testDirectory + "/LinearRegressionTemplate.ecl");
        ArrayList<String> finalDeps = new ArrayList<>();
        ArrayList<String> finalIndeps = new ArrayList<>();
        for (int i = 0; i < xCoefficients.size(); i++) {
            ArrayList<Double[]> dependents = new ArrayList<>();
            ArrayList<Double> independents = new ArrayList<>();

            //Create the dependent and independent values
            Double[] currCoef = xCoefficients.get(i);
            for (int j = 0; j < numPoints; j++) {
                Double[] newDependent = new Double[currCoef.length];
                newDependent[0] = currCoef[0];
                Double independent = newDependent[0];
                for (int k = 1; k < newDependent.length; k++) {
                    Double dependent = random.nextDouble() * 10;
                    newDependent[k] = dependent;
                    independent += dependent * currCoef[k];
                }

                dependents.add(newDependent);
                independents.add(independent);
            }

            assertEquals(dependents.size(), independents.size());

            ArrayList<String> depStrings = new ArrayList<>();
            ArrayList<String> indepStrings = new ArrayList<>();
            for (int j = 0; j < dependents.size(); j++) {
                Double[] dependentArray = dependents.get(j);
                for (int k = 1; k < dependentArray.length; k++) {
                    depStrings.add("{" + (i + 1) +", " + (j + 1) + ", " + (k) + ", " + dependentArray[k] + "}");
                }
                indepStrings.add("{" + (i + 1) +", " + (j + 1) + ", 1, " + independents.get(j) + "}");
            }
            finalDeps.add(String.join(",\n    ", depStrings));
            finalIndeps.add(String.join(",\n    ", indepStrings));
        }

        file = file.replaceFirst("dependentREPLACE", String.join(",\n    ", finalDeps));
        file = file.replaceFirst("independentREPLACE", String.join(",\n    ", finalIndeps));
        return file;
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
