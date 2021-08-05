package test.RandomForestTests;

import com.hpccsystems.pmml2ecl.ECLConverter;
import com.hpccsystems.pmml2ecl.PMMLConverter;
import org.junit.Test;
import test.CommonTestOperations;

import java.io.File;
import java.util.ArrayList;
import java.util.Random;

import static org.junit.Assert.*;

public class ClassificationForestTests {

    static String testDirectory = System.getProperty("user.dir") + "/src/main/java/test/RandomForestTests";
    static String outputDirectory = System.getProperty("user.dir") + "/output";

    @Test
    public void simpleECLtoPMMLTest() throws Exception {
//        new ECLConverter();
        new ECLConverter(testDirectory + "/ClassificationForestValidationTests.ecl");
    }

    @Test
    public void simplePMMLtoECLTest() throws Exception {
//        new PMMLConverter();
        new PMMLConverter(outputDirectory + "/PMMLOutput-ClassificationForest1.xml");
    }

}
