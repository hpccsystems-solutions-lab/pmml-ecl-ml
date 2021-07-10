package test;

import com.hpccsystems.pmml2ecl.ECLConverter;
import com.hpccsystems.pmml2ecl.PMMLConverter;
import com.hpccsystems.pmml2ecl.pmml.PMMLElement;
import com.hpccsystems.pmml2ecl.pmml.PMMLParser;

public class ParserTester {
    
    static String currDir = System.getProperty("user.dir");

    public static void main(String[] args) throws Exception {
//        endToEndECLLinearRegression();
//        endToEndPMMLLinearRegression();
        logisticRegressionECL();
//        logisticRegressionPMML();
//        classForest();
    }

    static void testPMMLParser() throws Exception {
        PMMLElement elem = new PMMLParser(currDir + "/src/main/java/test/resources/LinearRegression.xml").getRoot();
        System.out.println(elem.toString());
    }

    static void endToEndPMMLLinearRegression() throws Exception {
        new PMMLConverter(currDir + "/src/main/java/test/resources/LinearRegression.xml");
    }

    static void endToEndECLLinearRegression() throws Exception {
        new ECLConverter(currDir + "/src/main/java/test/LinearRegressionTests/MultipleWorkItemsLinear.ecl");
    }

    static void logisticRegressionECL() throws Exception {
        new ECLConverter(currDir + "/src/main/java/test/LogisticRegressionTests/LogisticRegression.ecl");
    }

    static void logisticRegressionPMML() throws Exception {
        new PMMLConverter(currDir + "/src/main/java/test/LogisticRegressionTests/SimpleLogisticRegression.xml");
    }

    static void classForest() throws Exception {

        String testDirectory = System.getProperty("user.dir") + "/src/main/java/test/RandomForestTests";
        new ECLConverter(testDirectory + "/ClassificationForestSimple.ecl");
    }

}
