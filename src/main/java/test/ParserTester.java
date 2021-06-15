package test;

import com.hpccsystems.pmml2ecl.ECLConverter;
import com.hpccsystems.pmml2ecl.PMMLConverter;
import com.hpccsystems.pmml2ecl.pmml.PMMLElement;
import com.hpccsystems.pmml2ecl.pmml.PMMLParser;

public class ParserTester {
    
    static String currDir = System.getProperty("user.dir");

    public static void main(String[] args) throws Exception {
//        endToEndECLLinearRegression();
        endToEndPMMLLinearRegression();
    }

    static void testPMMLParser() throws Exception {
        PMMLElement elem = new PMMLParser(currDir + "/src/test/resources/ExampleECLResult.xml").getRoot();
        System.out.println(elem.firstNodeWithTag("Dataset").toString());
    }

    // REPLACED W endToEndECLLinearRegression();
//    static void testXMLtoPMML() throws Exception {
//        PMMLElement eclXML = new PMMLParser(currDir + "/src/test/ECLDir/Return.xml").getRoot();
//        XMLMLConverter test = new XMLMLConverter(eclXML);
//        System.out.println(test.toLinearRegression().toString());
//    }
//
//    static void testLinearRegressionManual() throws Exception {
//        ECLParser parser = new ECLParser(currDir + "/src/main/java/test/ECLDir/Return.xml");
//    }
//
//    static void testCompiler() throws Exception {
//        ECLCompiler compiler = new ECLCompiler(currDir + "/src/main/java/test/LinearRegressionTest.ecl");
//    }

    static void endToEndPMMLLinearRegression() throws Exception {
        new PMMLConverter(currDir + "/src/main/java/test/BasicLinearRegression.xml");
    }

    static void endToEndECLLinearRegression() throws Exception {
        new ECLConverter(currDir + "/src/main/java/test/LinearRegressionTest.ecl");
    }

}
