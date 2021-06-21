package com.hpccsystems.pmml2ecl.ecl.algorithms;

import com.hpccsystems.pmml2ecl.Node;
import com.hpccsystems.pmml2ecl.pmml.PMMLElement;
import com.hpccsystems.pmml2ecl.pmml.operations.ElementFinder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LogisticRegression implements Algorithm {

    private PMMLElement rootECL;

    public LogisticRegression(PMMLElement rootECL) {
        this.rootECL = rootECL;
    }

    @Override
    public void writeStoredModel() throws Exception {
        PMMLElement model = rootECL.firstNodeWithTag("Dataset");

        List<PMMLElement> rows = new ArrayList<>();

        //Getting all the rows that correspond to
        for (Node node : model.childNodes) {
            PMMLElement idNode = ((PMMLElement) node).firstNodeWithTag("id");
            if (idNode != null) {
                try {
                    Integer value = Integer.parseInt(idNode.content);
                    if (value >= 5) {
                        rows.add((PMMLElement) node);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        int workid = 1;
        while (ElementFinder.hasElementWithTagContent(rows, "wi", Integer.toString(workid))) {
            List<PMMLElement> allBetasAndSE =
                    ElementFinder.getAllWhereHasTagContent(rows, "wi", Integer.toString(workid));
            int allSize = allBetasAndSE.size();
            if (allSize % 2 > 0) {
                System.out.println("Something went wrong. Betas and SE not even.");
                workid++;
                continue;
            }
            PMMLElement modelRoot = new PMMLElement("PMML",
                    "version=\"4.4\" xmlns=\"http://www.dmg.org/PMML-4_4\"", "", false);

            PMMLElement header = new PMMLElement("Header", "", "", true);
            modelRoot.addChild(header);

            Map<String, String> modelAttr = new HashMap<>();
            modelAttr.put("functionName", "classification");
            modelAttr.put("algorithmName", "LogisticRegression");
            PMMLElement generalRegressionModel =
                    new PMMLElement("GeneralRegressionModel", modelAttr, new ArrayList<>(), false);
            modelRoot.addChild(generalRegressionModel);

            int depnom = 1;
            List<Node> parameters = new ArrayList<>();
            while (ElementFinder.hasElementWithTagContent(allBetasAndSE, "number", Integer.toString(depnom))) {
                List<PMMLElement> betasWithDep =
                        ElementFinder.getAllWhereHasTagContent(allBetasAndSE, "number", Integer.toString(depnom));
                int depSize = betasWithDep.size();
                if (depSize % 2 > 0) {
                    System.out.println("Something went wrong. Number of rows in dependent column not even.");
                    depnom++;
                    continue;
                }
                List<PMMLElement> betas =
                        ElementFinder.getAllWhereHasTagInRange(betasWithDep, "id", 5, 5 + depSize / 2);
                parameters.addAll(getSubParamMatrix(betas));
                depnom++;
            }
            PMMLElement paramMatrix =
                    new PMMLElement("ParamMatrix", new HashMap<>(), new ArrayList<>(), false);
            paramMatrix.addChildren(parameters);
            generalRegressionModel.addChild(paramMatrix);
            modelRoot.writeToFile("LogisticRegression" + workid);
            workid++;
        }
    }

    private List<PMMLElement> getSubParamMatrix(List<PMMLElement> betas) {
        List<PMMLElement> elements = new ArrayList<>();
        for (PMMLElement beta : betas) {
            String paramName = beta.firstNodeWithTag("id").content;
            try {
                paramName = "p" + (Integer.parseInt(paramName) - 5);
            } catch (Exception e) {
                e.printStackTrace();
            }
            String targetCategory = beta.firstNodeWithTag("number").content;
            String betaValue = beta.firstNodeWithTag("value").content;
            Map<String, String> paramAttr = new HashMap<>();
            paramAttr.put("parameterName", paramName);
            paramAttr.put("targetCategory", targetCategory);
            paramAttr.put("beta", betaValue);
            paramAttr.put("df", "1");
            elements.add(new PMMLElement("PCell", paramAttr, new ArrayList<>(), true));
        }

        return elements;
    }


}
