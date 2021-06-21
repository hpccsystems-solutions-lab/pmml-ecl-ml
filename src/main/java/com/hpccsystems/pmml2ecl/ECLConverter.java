package com.hpccsystems.pmml2ecl;

import com.hpccsystems.pmml2ecl.ecl.ECLCompiler;
import com.hpccsystems.pmml2ecl.ecl.ECLParser;

public class ECLConverter {

    private static final String currDir = System.getProperty("user.dir");

    /**
     * Runs through the lifecycle of compiling the ECL, getting it's results and parsing the result XML.
     * The result is a PMML file in the /output folder.
     * @param absoluteFilePath the .ECL file to be run
     * @throws Exception
     */
    public ECLConverter(String absoluteFilePath) throws Exception {
        try {
            new ECLCompiler(absoluteFilePath);
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("Compiling ECL at " + absoluteFilePath +  " failed.");
        }
        try {
            new ECLParser(currDir + "/obj/CompileResult.xml");
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("Could not parse result XML file.");
        }
    }

}
