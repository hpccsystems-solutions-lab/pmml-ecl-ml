package com.hpccsystems.pmml2ecl;

import com.hpccsystems.pmml2ecl.ecl.ECLCompiler;
import com.hpccsystems.pmml2ecl.ecl.ECLParser;

public class ECLConverter {

    private static String currDir = System.getProperty("user.dir");

    public ECLConverter(String absoluteFilePath) throws Exception {
        try {
            new ECLCompiler(absoluteFilePath);
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("Compiling ECL at " + absoluteFilePath +  " failed.");
        }
        try {
            new ECLParser(currDir + "/obj/CompileResult.xml").writeToOutput();
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("Could not parse result XML file.");
        }
    }

}
