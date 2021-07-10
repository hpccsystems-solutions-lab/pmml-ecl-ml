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

public class LinearRegression implements Algorithm {

    private PMMLElement rootModel;

    public LinearRegression(PMMLElement rootModel) {
        this.rootModel = rootModel;
    }

    @Override
    public void writeStoredModel() throws Exception {
        List<PMMLElement> elements = new ArrayList<>();
        for (Node n : rootModel.childNodes) {
            elements.add((PMMLElement) n);
        }
        int workid = 1;
        while (ElementFinder.hasElementWithTagContent(elements, "wi", Integer.toString(workid))) {
            getFile(ElementFinder.getAllWhereHasTagContent(elements, "wi", Integer.toString(workid)))
                    .writeToFile("LinearRegression", workid);
            workid++;
        }
    }

    @Override
    public void writeStoredModel(String absoluteFilePath) throws Exception {
        if (absoluteFilePath == null) {
            writeStoredModel();
            return;
        }
        List<PMMLElement> elements = new ArrayList<>();
        for (Node n : rootModel.childNodes) {
            elements.add((PMMLElement) n);
        }
        int workid = 1;
        while (ElementFinder.hasElementWithTagContent(elements, "wi", Integer.toString(workid))) {
            getFile(ElementFinder.getAllWhereHasTagContent(elements, "wi", Integer.toString(workid)))
                    .writeToFile(FileNames.insertNumberToFilePath(absoluteFilePath, workid));
            workid++;
        }
    }

    private PMMLElement getFile(List<PMMLElement> elementsToWorkOn) {
        PMMLElement linearRegressionRoot = new PMMLElement("PMML",
                "version=\"4.4\" xmlns=\"http://www.dmg.org/PMML-4_4\"", "", false);

        PMMLElement header = new PMMLElement("Header", "", "", true);

        linearRegressionRoot.addChild(header);

        List<Node> fields = new ArrayList<>();
        List<Node> coefficients = new ArrayList<>();
        String intercept = "";

        for(Node elem : elementsToWorkOn) {
            String field = ((PMMLElement) elem).firstNodeWithTag("number").content;
            String value = ((PMMLElement) elem).firstNodeWithTag("value").content;
            if (field.equals("1")) {
                intercept = value;
                continue;
            }
            Map<String, String> attribs = new HashMap<>();
            attribs.put("name", field);
            fields.add(new PMMLElement("MiningField", attribs, null, true));
            Map<String, String> attribs2 = new HashMap<>();
            attribs2.put("name", field);
            attribs2.put("coefficient", value);
            coefficients.add(new PMMLElement("NumericPredictor", attribs2, null, true));
        }

        fields.add(CommonElements.createNewComment("Note that MiningSchema names MUST correspond to NumericPredictor names."));
        PMMLElement schema = new PMMLElement("MiningSchema", new HashMap<>(), fields, false);

        Map<String, String> tableAttr = new HashMap<>();
        tableAttr.put("intercept", intercept);
        PMMLElement table = new PMMLElement("RegressionTable", tableAttr, coefficients, false);

        Map<String, String> regMap = new HashMap<>();
        regMap.put("functionName", "regression");
        regMap.put("algorithmName", "LinearRegression");
        PMMLElement regModel = new PMMLElement("RegressionModel", regMap, new ArrayList<>(), false);

        regModel.addChild(schema);
        regModel.addChild(table);

        linearRegressionRoot.addChild(regModel);
        return linearRegressionRoot;
    }
}
