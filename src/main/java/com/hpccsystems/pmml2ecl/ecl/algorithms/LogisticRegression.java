package com.hpccsystems.pmml2ecl.ecl.algorithms;

import com.hpccsystems.pmml2ecl.Node;
import com.hpccsystems.pmml2ecl.pmml.PMMLElement;
import com.hpccsystems.pmml2ecl.pmml.operations.CommonElements;
import com.hpccsystems.pmml2ecl.pmml.operations.ElementFinder;
import com.hpccsystems.pmml2ecl.pmml.operations.FileNames;

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

            int depnom = 1;
            List<Node> parameters = new ArrayList<>();
            List<Node> pps = new ArrayList<>();
            List<Node> categories = new ArrayList<>();
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
                if (depnom == 1) pps.addAll(getSubPPMatrix(betas));
                Map<String, String> catAttr = new HashMap<>();
                catAttr.put("value", Integer.toString(depnom));
                categories.add(new PMMLElement("Value", catAttr, new ArrayList<>(), true));
                depnom++;
            }

            //TODO: Add support for a DataDictionary.
//            PMMLElement dataDict = CommonElements.emptyElement("DataDictionary");
//            Map<String, String> dataAttr = new HashMap<>();
//            dataAttr.put("dataType", "string");
//            dataAttr.put("name", "class");
//            dataAttr.put("optype", "categorical");
//            PMMLElement dataCategories = new PMMLElement("DataField", dataAttr, categories, false);
//            dataDict.addChild(dataCategories);
//            modelRoot.addChild(dataDict);

            PMMLElement ppMatrix = CommonElements.emptyElement("PPMatrix");
            ppMatrix.addChildren(pps);

            PMMLElement paramMatrix = CommonElements.emptyElement("ParamMatrix");
            paramMatrix.addChildren(parameters);

            PMMLElement covariate = CommonElements.emptySelfClosedElement("CovariateList");

            PMMLElement factors =
                    new PMMLElement("FactorList", new HashMap<>(), new ArrayList<>(), true);

            //TODO: Add Mining Schema
            generalRegressionModel.addChild(factors);
            generalRegressionModel.addChild(covariate);
            generalRegressionModel.addChild(ppMatrix);
            generalRegressionModel.addChild(paramMatrix);
            modelRoot.addChild(generalRegressionModel);
            modelRoot.writeToFile("LogisticRegression", workid);
            workid++;
        }
    }

    @Override
    public void writeStoredModel(String absoluteFilePath) throws Exception {
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

            int depnom = 1;
            List<Node> parameters = new ArrayList<>();
            List<Node> pps = new ArrayList<>();
            List<Node> categories = new ArrayList<>();
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
                if (depnom == 1) pps.addAll(getSubPPMatrix(betas));
                Map<String, String> catAttr = new HashMap<>();
                catAttr.put("value", Integer.toString(depnom));
                categories.add(new PMMLElement("Value", catAttr, new ArrayList<>(), true));
                depnom++;
            }

            //TODO: Add support for a DataDictionary.
//            PMMLElement dataDict = CommonElements.emptyElement("DataDictionary");
//            Map<String, String> dataAttr = new HashMap<>();
//            dataAttr.put("dataType", "string");
//            dataAttr.put("name", "class");
//            dataAttr.put("optype", "categorical");
//            PMMLElement dataCategories = new PMMLElement("DataField", dataAttr, categories, false);
//            dataDict.addChild(dataCategories);
//            modelRoot.addChild(dataDict);

            PMMLElement ppMatrix = CommonElements.emptyElement("PPMatrix");
            ppMatrix.addChildren(pps);

            PMMLElement paramMatrix = CommonElements.emptyElement("ParamMatrix");
            paramMatrix.addChildren(parameters);

            PMMLElement covariate = CommonElements.emptySelfClosedElement("CovariateList");

            PMMLElement factors =
                    new PMMLElement("FactorList", new HashMap<>(), new ArrayList<>(), true);

            //TODO: Add Mining Schema
            generalRegressionModel.addChild(factors);
            generalRegressionModel.addChild(covariate);
            generalRegressionModel.addChild(ppMatrix);
            generalRegressionModel.addChild(paramMatrix);
            modelRoot.addChild(generalRegressionModel);
            modelRoot.writeToFile(FileNames.insertNumberToFilePath(absoluteFilePath, workid));
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

    private List<PMMLElement> getSubPPMatrix(List<PMMLElement> betas) {
        List<PMMLElement> elements = new ArrayList<>();
        for (PMMLElement beta : betas) {
            String paramName = beta.firstNodeWithTag("id").content;
            try {
                paramName = "p" + (Integer.parseInt(paramName) - 5);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (!paramName.equals("p0")) {
                Map<String, String> paramAttr = new HashMap<>();
                paramAttr.put("parameterName", paramName);
                paramAttr.put("predictorName", "");
                paramAttr.put("value", "1");
                elements.add(new PMMLElement("PPCell", paramAttr, new ArrayList<>(), true));
            }
        }
        elements.add(CommonElements.createNewComment("Change predictorName for convenience."));
        return elements;
    }


}
