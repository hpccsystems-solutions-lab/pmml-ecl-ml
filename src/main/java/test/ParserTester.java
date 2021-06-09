package test;

import com.hpccsystems.pmml2ecl.pmml.PMMLElement;
import com.hpccsystems.pmml2ecl.pmml.PMMLParser;

import java.util.LinkedList;

import com.hpccsystems.pmml2ecl.ecl.ECLCompiler;
import com.hpccsystems.pmml2ecl.ecl.ECLElement;
import com.hpccsystems.pmml2ecl.ecl.ECLParser;
import com.hpccsystems.pmml2ecl.ecl.XMLMLConverter;

public class ParserTester {
    
    static String currDir = System.getProperty("user.dir");

    public static void main(String[] args) throws Exception {
        testCompiler();
    }

    static void testPMML() throws Exception {
        PMMLElement elem = (PMMLElement) new PMMLParser(currDir + "/src/test/resources/ExampleECLResult.xml").getRoot(); 
        System.out.println(elem.firstNodeWithTag("Dataset").toString());
    }

    static void testECL() {
        LinkedList<ECLElement> allElems = new ECLParser("r := {STRING1 Letter};\nds1 := DATASET([{'A'},{'B'},{'C'},{'D'},{'E'}],r);\nds2 := DATASET([{'F'},{'G'},{'H'},{'I'},{'J'}],r);" +
        "ds3 := DATASET([{'K'},{'L'},{'M'},{'N'},{'O'}],r);\nds4 := DATASET([{'P'},{'Q'},{'R'},{'S'},{'T'}],r);\nds5 := DATASET([{'U'},{'V'},{'W'},{'X'},{'Y'}],r);", true).getElems();
        while (!allElems.isEmpty()) {
            System.out.println(allElems.pop().toString());
        }
    }

    static void testXMLtoPMML() throws Exception {
        PMMLElement eclXML = new PMMLParser(currDir + "/src/test/ECLDir/Return.xml").getRoot();
        XMLMLConverter test = new XMLMLConverter(eclXML);
        System.out.println(test.toLinearRegression().toString());
    }

    static void testLinearRegressionManual() throws Exception {
        ECLParser parser = new ECLParser(currDir + "/src/main/java/test/ECLDir/Return.xml");
    }

    static void testCompiler() throws Exception {
        ECLCompiler compiler = new ECLCompiler(currDir + "/src/main/java/test/LinearRegressionTest.ecl");
    }

}
