package com.hpccsystems.pmml2ecl.pmml.algorithms;

import com.hpccsystems.pmml2ecl.Node;
import com.hpccsystems.pmml2ecl.ecl.ECLElement;
import com.hpccsystems.pmml2ecl.pmml.PMMLElement;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class LinearRegression implements Algorithm {

    private PMMLElement model;

    public LinearRegression(PMMLElement model) {
        this.model = model;
    }

    @Override
    public LinkedList<ECLElement> getEclFromModel() throws Exception {
        LinkedList<ECLElement> modelECL = new LinkedList<>();
        PMMLElement schema = model.firstNodeWithTag("MiningSchema");
        PMMLElement table = model.firstNodeWithTag("RegressionTable");
        if (table == null) {
            table = model.firstNodeWithTag("GeneralRegressionTable");
        }

        modelECL.add(new ECLElement("IMPORT LinearRegression as LR;"));
        String finalElement = "model := DATASET([";

        List<String> dataPoints;
        if (table.attributes.containsKey("intercept")) {
            dataPoints = getFromInterceptTable(schema, table);
        } else {
            dataPoints = getFromSequentialTable(schema, table);
        }

        finalElement += String.join(",\n    ", dataPoints.subList(0, dataPoints.size() - 1)) + "], Types.Layout_Model);\n";
        modelECL.add(new ECLElement(finalElement));
        modelECL.add(new ECLElement(dataPoints.get(dataPoints.size() - 1)));

        modelECL.add(new ECLElement("linearRegression := LR.OLS();"));
        modelECL.add(new ECLElement("//Use `linearRegression.Predict(matrixNF, model);` to predict new values after this line."));
        return modelECL;
    }

    private static List<String> getFromInterceptTable(PMMLElement schema, PMMLElement table) {
        List<String> stringsToAdd = new ArrayList<>();
        String finalTable = "";
        stringsToAdd.add("{1, 1, 1, " + table.getValue("intercept") + "}");
        finalTable += "// X1 (intercept) - " + table.getValue("intercept") + "\n";
        int counter = 2;
        for (Node child : schema.childNodes) {
            String keyName = ((PMMLElement) child).getValue("name");
            PMMLElement tableNode = table.firstNodeWithAttribute("name", keyName);
            if (tableNode != null) {
                String value = tableNode.getValue("coefficient");
                finalTable += "// X" + counter + " - " + value + " (" + keyName + ")\n";
                stringsToAdd.add("{1, 1, " + counter + ", " + value + "}");
                counter++;
            }
        }
        stringsToAdd.add(finalTable);
        return stringsToAdd;
    }

    private static List<String> getFromSequentialTable(PMMLElement schema, PMMLElement table) {
        List<String> stringsToAdd = new ArrayList<>();
        String finalTable = "";
        int counter = 1;
        for (Node child : schema.childNodes) {
            String keyName = ((PMMLElement) child).getValue("name");
            PMMLElement tableNode = table.firstNodeWithAttribute("name", keyName);
            if (tableNode != null) {
                String value = table.firstNodeWithAttribute("name", keyName).getValue("coefficient");
                finalTable += "// X" + counter + " - " + value + "(" + keyName + ")\n";
                stringsToAdd.add("{1, 1, " + counter + ", " + value + "}");
                counter++;
            }
        }
        stringsToAdd.add(finalTable);
        return stringsToAdd;
    }

}
