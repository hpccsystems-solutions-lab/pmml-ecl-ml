package com.hpccsystems.pmml2ecl.pmml;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.hpccsystems.pmml2ecl.Node;
import com.hpccsystems.pmml2ecl.ecl.ECLElement;

public class PMMLConverter {
    
    //find first with functionName/algorithmName
    //get functionName and tag
    //
    LinkedList<ECLElement> ecl;
    public PMMLConverter(PMMLElement root) {
        PMMLElement model = root.firstNodeWithTag("RegressionModel");
        String functionName = model.getValue("algorithmName");
        ecl = new LinkedList<>();
        ecl.add(new ECLElement("IMPORT ML_Core;"));
        ecl.add(new ECLElement("IMPORT ML_Core.Types;"));
        switch (functionName) {
            case "LinearRegression":
                ecl.addAll(getEclFromLinearRegression(model));
                break;
            default:
                break;
        }
    }

    private LinkedList<ECLElement> getEclFromLinearRegression(PMMLElement model) {
        LinkedList<ECLElement> modelECL = new LinkedList<>();
        PMMLElement schema = model.firstNodeWithTag("MiningSchema");
        PMMLElement table = model.firstNodeWithTag("RegressionTable");

        //Just for the DATASET
        String finalElement = "DATASET([";
        List<String> dataPoints = new ArrayList<>();
        for (Node child : schema.childNodes) {
            String keyName = ((PMMLElement) child).getValue("name");
            String value = table.firstNodeWithKey(keyName).getValue(keyName);
            //{1, 1, .9999}
            //{1, 2, 2.0000}
            dataPoints.add("{1, " + value + ", [" + keyName + "]}");
        }
        finalElement += String.join(",", dataPoints) + "], Layout_Model2);";

        return modelECL;
    }

    public String eclToString() {
        String full = "";
        for (int i = 0; i < ecl.size(); i++) {
            full += ecl.get(i).toString() + ";\n";
        }
        return full;
    }

}
