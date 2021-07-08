package com.hpccsystems.pmml2ecl.ecl.algorithms;

import com.hpccsystems.pmml2ecl.Node;
import com.hpccsystems.pmml2ecl.pmml.PMMLElement;
import com.hpccsystems.pmml2ecl.pmml.operations.ElementFinder;

import java.util.*;

public class ClassificationForest implements Algorithm{



    private PMMLElement rootECL;

    public ClassificationForest(PMMLElement rootECL) {
        this.rootECL = rootECL;
    }

    @Override
    public void writeStoredModel() throws Exception {
        writeStoredModel(null);
    }

    @Override
    public void writeStoredModel(String absoluteFilePath) throws Exception {
        List<PMMLElement> storedModels = getStoredModels();
        if (absoluteFilePath != null) {
            for (PMMLElement model : storedModels) {
                model.writeToFile(absoluteFilePath);
            }
        } else {

        }
    }

    private List<PMMLElement> getStoredModels() {
        PMMLElement model = rootECL.firstNodeWithKey("Dataset");

        List<PMMLElement> elementsToWorkOn = new ArrayList<>();

        for (Node n : model.childNodes) {
            elementsToWorkOn.add((PMMLElement) n);
        }

        int counter = 1; //0 has useless indices???
        while (ElementFinder.hasElementWithTagContent(elementsToWorkOn, "wi", Integer.toString(counter))) {
            List<PMMLElement> wiElements =
                    ElementFinder.getAllWhereHasTagContent(elementsToWorkOn, "wi", Integer.toString(counter));
        }

        finalModel.addChild(treeModel);

        return finalModel;
    }

    private PMMLElement getTreeFromElements(List<PMMLElement> elementsToWorkOn) {

        PMMLElement finalModel = new PMMLElement("PMML",
                "version=\"4.4\" xmlns=\"http://www.dmg.org/PMML-4_4\"", "", false);

        //modelName="randomForest_Model" functionName="classification" algorithmName="randomForest"
        Map<String, String> treeParams = new HashMap<>();
        treeParams.put("modelName", "randomForest_Model");
        treeParams.put("functionName", "classification");
        treeParams.put("algorithmName", "randomForest");
        PMMLElement treeModel = new PMMLElement("TreeModel", treeParams, new ArrayList<>(), false);
    }

}
