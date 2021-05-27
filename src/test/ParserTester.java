package test;

import com.hpccsystems.pmml2ecl.pmml.PMMLElement;
import com.hpccsystems.pmml2ecl.pmml.PMMLParser;
import com.hpccsystems.pmml2ecl.Node;

public class ParserTester {
    
    public static void main(String[] args) throws Exception {
        // System.out.println(new PMMLParser("<Root bruh=\"uim\"><Node>45</Node><Node>67</Node></Root>", true).getRoot().toString());
        System.out.println(System.getProperty("user.dir"));
        PMMLElement elem = (PMMLElement) new PMMLParser(System.getProperty("user.dir") + "/src/test/resources/LinearRegression.xml").getRoot();
        // System.out.println(elem.toString());
        for (Node n : elem.childNodes.get(2).childNodes.get(0).childNodes) {
            System.out.println(n.attributes.get("name"));
        }
    }

}
