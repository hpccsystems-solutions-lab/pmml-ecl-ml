package test;

import com.hpccsystems.pmml2ecl.pmml.PMMLElement;
import com.hpccsystems.pmml2ecl.pmml.PMMLParser;

import java.util.LinkedList;
import java.util.List;

import com.hpccsystems.pmml2ecl.Node;
import com.hpccsystems.pmml2ecl.ecl.ECLElement;
import com.hpccsystems.pmml2ecl.ecl.ECLParser;
import com.hpccsystems.pmml2ecl.ecl.XMLMLConverter;

public class ParserTester {
    
    static String currDir = System.getProperty("user.dir");

    public static void main(String[] args) throws Exception {
        testXMLtoPMML();
    }

    static void testPMML() throws Exception {
        PMMLElement elem = (PMMLElement) new PMMLParser(currDir + "/src/test/resources/ExampleECLResult.xml").getRoot(); 
        System.out.println(elem.firstNodeWithTag("Dataset").toString());
        // System.out.println(elem.firstNodeWithKey("functionName").toString());
        // System.out.println(elem.firstNodeWithAttribute("name", "Gender").toString());
        // System.out.println(elem.allNodesMatchingAttribute("dataType", "integer"));
    }

    static void testECL() {
        LinkedList<ECLElement> allElems = new ECLParser("r := {STRING1 Letter};\nds1 := DATASET([{'A'},{'B'},{'C'},{'D'},{'E'}],r);\nds2 := DATASET([{'F'},{'G'},{'H'},{'I'},{'J'}],r);" +
        "ds3 := DATASET([{'K'},{'L'},{'M'},{'N'},{'O'}],r);\nds4 := DATASET([{'P'},{'Q'},{'R'},{'S'},{'T'}],r);\nds5 := DATASET([{'U'},{'V'},{'W'},{'X'},{'Y'}],r);", true).getElems();
        while (!allElems.isEmpty()) {
            System.out.println(allElems.pop().toString());
        }
    }

    static void testXMLtoPMML() throws Exception {
        PMMLElement eclXML = (PMMLElement) new PMMLParser(currDir + "/src/test/ECLDir/Return.xml").getRoot();
        XMLMLConverter test = new XMLMLConverter(eclXML);
        System.out.println(test.toLinearRegression().toString());
    }

}
