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
    public void simpleTest() throws Exception {
        new ECLConverter(testDirectory + "/ClassificationForestSimple.ecl");
    }

}
