package test.PMMLTests;

import com.hpccsystems.pmml2ecl.ecl.ECLCompiler;
import com.hpccsystems.pmml2ecl.pmml.PMMLParser;
import org.junit.Test;

public class PMMLTests {

    static String testDirectory = System.getProperty("user.dir") + "/src/main/java/test/PMMLTests";
    static String outputDirectory = System.getProperty("user.dir") + "/output";

    @Test
    public void parserTests() throws Exception {

        System.out.println(new PMMLParser(testDirectory + "/testPMML.xml").getRoot().toString());

    }

    @Test
    public void extraTests() throws Exception {

        System.out.println(new PMMLParser(testDirectory + "/testCases.xml").getRoot().toString());

    }

    @Test
    public void simpleTests() throws Exception {
        System.out.println(new PMMLParser(testDirectory + "/../LogisticRegressionTests/SimpleLogisticRegression.xml").getRoot().toString());
    }

}
