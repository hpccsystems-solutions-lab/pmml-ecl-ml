package com.hpccsystems.pmml2ecl;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.hpccsystems.pmml2ecl.Node;
import com.hpccsystems.pmml2ecl.ecl.ECLElement;
import com.hpccsystems.pmml2ecl.ecl.ECLParser;
import com.hpccsystems.pmml2ecl.pmml.PMMLElement;
import com.hpccsystems.pmml2ecl.pmml.PMMLParser;

public class PMMLConverter {

    private static final String currDir = System.getProperty("user.dir");
    private LinkedList<ECLElement> ecl;

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
                ecl.addAll(getEclFromLinearRegression(model));
                break;
            default:
                break;
        }
        ECLParser.writeToFile(ecl);
    }

    /**
     * Converts a root PMMLElement into its corresponding ECL and is outputted in /output.
     * @param root The root of the Element you want to be converted.
     * @throws Exception
     */
    public PMMLConverter(PMMLElement root) throws Exception {
        PMMLElement model = root.firstNodeWithTag("RegressionModel");
        String functionName = model.getValue("algorithmName");
        ecl = new LinkedList<>();
        ecl.add(new ECLElement("IMPORT ML_Core;"));
        ecl.add(new ECLElement("IMPORT ML_Core.Types;"));
        ecl.add(new ECLElement("IMPORT ML_Core.ModelOps2 as ModelOps2;"));
        switch (functionName) {
            case "LinearRegression":
                ecl.addAll(getEclFromLinearRegression(model));
                break;
            default:
                break;
        }
        ECLParser.writeToFile(ecl);
    }

    private LinkedList<ECLElement> getEclFromLinearRegression(PMMLElement model) {
        LinkedList<ECLElement> modelECL = new LinkedList<>();
        PMMLElement schema = model.firstNodeWithTag("MiningSchema");
        PMMLElement table = model.firstNodeWithTag("RegressionTable");

        modelECL.add(new ECLElement("IMPORT LinearRegression as LR;"));
        //Just for the DATASET
        String finalElement = "model := DATASET([";
        List<String> dataPoints = new ArrayList<>();
        for (Node child : schema.childNodes) {
            String keyName = ((PMMLElement) child).getValue("name");
            String value = table.firstNodeWithAttribute("name", keyName).getValue("coefficient");
            dataPoints.add("{1, 1, " + keyName + ", " + value + "}");
        }
        finalElement += String.join(",\n    ", dataPoints) + "], Types.Layout_Model);";
        modelECL.add(new ECLElement(finalElement));

        modelECL.add(new ECLElement("linearRegression := LR.OLS();"));
        modelECL.add(new ECLElement("//Use `linearRegression.Predict(matrixNF, model);` to predict new values after this line."));
        return modelECL;
    }

    public LinkedList<ECLElement> getECL() {
        return ecl;
    }

}
