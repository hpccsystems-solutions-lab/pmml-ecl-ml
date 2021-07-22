package test.CompilerTests;

import com.hpccsystems.pmml2ecl.ecl.ECLCompiler;
import org.junit.Test;

public class CompilerTests {

    static String testDirectory = System.getProperty("user.dir") + "/src/main/java/test/CompilerTests";
    static String outputDirectory = System.getProperty("user.dir") + "/output";

    @Test
    public void compilerTest() throws Exception {

        new ECLCompiler(testDirectory + "/TestECL.ecl");

    }

}
