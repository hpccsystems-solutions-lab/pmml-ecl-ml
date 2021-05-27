package test;

import com.hpccsystems.pmml2ecl.pmml.PMMLElement;
import com.hpccsystems.pmml2ecl.pmml.PMMLParser;
import com.hpccsystems.pmml2ecl.Node;

public class ParserTester {
    
    static String currDir = System.getProperty("user.dir");

    public static void main(String[] args) throws Exception {
        testPMML();
    }

    static void testPMML() throws Exception {
        PMMLElement elem = (PMMLElement) new PMMLParser(currDir + "/src/test/resources/LinearRegression.xml").getRoot(); 
        // System.out.println(elem.firstNodeWithKey("functionName").toString());
        // System.out.println(elem.firstNodeWithAttribute("name", "Gender").toString());
        // System.out.println(elem.allNodesMatchingAttribute("dataType", "integer"));
    }

    static void testECL() {

    }

}
