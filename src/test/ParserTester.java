package test;


import com.hpccsystems.pmml2ecl.pmml.PMMLParser;

public class ParserTester {
    
    public static void main(String[] args) throws Exception {
        // System.out.println(new PMMLParser("<Root bruh=\"uim\"><Node>45</Node><Node>67</Node></Root>", true).getRoot().toString());
        System.out.println(System.getProperty("user.dir"));
        System.out.println(new PMMLParser(System.getProperty("user.dir") + "/src/test/resources/LinearRegression.xml").getRoot().toString());
    }

}
