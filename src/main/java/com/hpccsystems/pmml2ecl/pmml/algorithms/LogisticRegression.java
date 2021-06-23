package com.hpccsystems.pmml2ecl.pmml.algorithms;

import com.hpccsystems.pmml2ecl.Node;
import com.hpccsystems.pmml2ecl.ecl.ECLElement;
import com.hpccsystems.pmml2ecl.pmml.PMMLElement;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class LogisticRegression implements Algorithm {

    private PMMLElement model;

    public LogisticRegression(PMMLElement model) {
        this.model = model;
    }

    @Override
    public LinkedList<ECLElement> getEclFromModel() throws Exception {
        LinkedList<ECLElement> modelECL = new LinkedList<>();
        PMMLElement table = model.firstNodeWithTag("ParamMatrix");

        modelECL.add(new ECLElement("IMPORT LogisticRegression as LR;"));
        String betas = "betas := DATASET([\n    ";

        //TODO: This is where we mine and parse the PMML.
        List<String> dataPoints;
        dataPoints = getFromSequentialTable(table);

        betas += String.join(",\n    ", dataPoints.subList(0, dataPoints.size() - 1));

        betas += "], LR.Types.Model_Coef);\n";

        modelECL.add(new ECLElement(betas));
        modelECL.add(new ECLElement(dataPoints.get(dataPoints.size() - 1)));

        modelECL.add(new ECLElement("//Use `LR.LogitPredict(betas, matrixNF);` to predict new values after this line."));
        return modelECL;
    }

    private static List<String> getFromSequentialTable(PMMLElement paramMatrix) throws Exception {
        List<String> stringsToAdd = new ArrayList<>();
        String finalTable = "";
        for (Node child : paramMatrix.childNodes) {
            PMMLElement pmmlChild = (PMMLElement) child;

            String paramName = pmmlChild.getValue("parameterName");
            if (paramName == null) throw new Exception("parameterName not defined in a node in ParamMatrix");
            paramName = paramName.substring(1);

            String targetCat = pmmlChild.getValue("targetCategory");
            if (targetCat == null) throw new Exception("targetCategory not defined in a node in ParamMatrix");

            String beta = pmmlChild.getValue("beta");
            if (beta == null) throw new Exception("beta value not defined in a node in ParamMatrix");

            //wi, paramname, targetcat, beta, 0
            finalTable += "// X" + paramName + " for category " + targetCat + " - " + beta + "\n";
            stringsToAdd.add("{1, " + paramName + ", " + targetCat + ", " + beta + ", 0}");
        }
        stringsToAdd.add(finalTable);
        return stringsToAdd;
    }

}
