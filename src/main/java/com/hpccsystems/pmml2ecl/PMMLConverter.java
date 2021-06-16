package com.hpccsystems.pmml2ecl;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.hpccsystems.pmml2ecl.Node;
import com.hpccsystems.pmml2ecl.ecl.ECLElement;
import com.hpccsystems.pmml2ecl.ecl.ECLParser;
import com.hpccsystems.pmml2ecl.pmml.PMMLElement;
import com.hpccsystems.pmml2ecl.pmml.PMMLParser;
import com.hpccsystems.pmml2ecl.pmml.algorithms.LinearRegression;

public class PMMLConverter {

    private LinkedList<ECLElement> ecl;

    /**
     * Intakes a file path for a .xml/pmml file to convert into a .ecl file
     * with the corresponding ML model as a variable. The final file resides in /output.
     * @param absoluteFilePath the path of the .xml/.pmml file to convert.
     * @throws Exception
     */
    public PMMLConverter(String absoluteFilePath) throws Exception {
        PMMLParser parser = new PMMLParser(absoluteFilePath);
        PMMLElement root = parser.getRoot();
        PMMLElement model = root.firstNodeWithTag("RegressionModel");
        String functionName = model.getValue("algorithmName");
        ecl = new LinkedList<>();
        ecl.add(new ECLElement("IMPORT ML_Core;"));
        ecl.add(new ECLElement("IMPORT ML_Core.Types;"));
        ecl.add(new ECLElement("IMPORT ML_Core.ModelOps2 as ModelOps2;"));
        switch (functionName) {
            case "LinearRegression":
                ecl.addAll(LinearRegression.getEclFromModel(model));
                break;
            default:
                break;
        }
        ECLParser.writeToFile(ecl);
    }

}
