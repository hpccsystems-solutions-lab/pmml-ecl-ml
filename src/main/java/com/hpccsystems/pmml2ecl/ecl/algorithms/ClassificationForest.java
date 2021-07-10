package com.hpccsystems.pmml2ecl.ecl.algorithms;

import com.hpccsystems.pmml2ecl.Node;
import com.hpccsystems.pmml2ecl.pmml.PMMLElement;
import com.hpccsystems.pmml2ecl.pmml.operations.CommonElements;
import com.hpccsystems.pmml2ecl.pmml.operations.ElementFinder;
import com.hpccsystems.pmml2ecl.pmml.operations.FileNames;

import java.util.*;

public class ClassificationForest implements Algorithm {

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
        for (int i = 1; i < storedModels.size() + 1; i++)
            if (absoluteFilePath != null)
                storedModels.get(i - 1).writeToFile(FileNames.insertNumberToFilePath(absoluteFilePath, i));
            else
                storedModels.get(i - 1).writeToFile("ClassificationForest", i);
    }

    private List<PMMLElement> getStoredModels() {
        PMMLElement model = rootECL.firstNodeWithKey("Dataset");

        List<PMMLElement> elementsToWorkOn = new ArrayList<>();
        List<PMMLElement> finalModels = new ArrayList<>();

        for (Node n : model.childNodes) {
            elementsToWorkOn.add((PMMLElement) n);
        }

        int counter = 1; //0 has useless indices???
        while (ElementFinder.hasElementWithTagContent(elementsToWorkOn, "wi", Integer.toString(counter))) {
            List<PMMLElement> wiElements =
                    ElementFinder.getAllWhereHasTagContent(elementsToWorkOn, "wi", Integer.toString(counter));
            List<PMMLElement> relevantElements = getAllWithSpecificIndexItem(wiElements, 0, "2");
            System.out.print("1..");
            finalModels.add(getTreeFromElements(relevantElements));
            counter++;
        }

        return finalModels;
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

        finalModel.addChild(treeModel);

        //TODO: Iterate over elements and decide what to do with them.
        Map<String, String> baseNodeAttr = new HashMap<>();
        baseNodeAttr.put("id", "1");
        PMMLElement baseNode = new PMMLElement("Node", baseNodeAttr, new ArrayList<>(), false);
        baseNode.addChild(CommonElements.emptySelfClosedElement("True"));

        treeModel.addChild(baseNode);

        //TODO: Add children to Base Node. RECURSIVELY?
        //      Iterate over all "Items" and then create nodes for them.
        //      Do it destructively to cut down on time? Eh do it regularly first
        int counter = 1;
        while (hasSpecificIndexItem(elementsToWorkOn, 1, Integer.toString(counter))) {
            List<PMMLElement> listOfCounter =
                    getAllWithSpecificIndexItem(elementsToWorkOn, 1, Integer.toString(counter));
            PMMLElement node = CommonElements.emptyElement("Node");
            node.attributes.put("id", getFirstWithIndexItem(listOfCounter, 2, "3").firstNodeWithTag("value").content);
            System.out.print("2..");
            System.out.println(node.toString());
            counter++;
        }

        return finalModel;
    }

    private List<PMMLElement> getAllWithSpecificIndexItem(List<PMMLElement> elements, int itemNum, String value) {
        List<PMMLElement> finalElems = new ArrayList<>();
        for (PMMLElement elem : elements) {
            PMMLElement indices = elem.firstNodeWithTag("indexes");
            if (indices != null &&
                    indices.childNodes.size() > itemNum && indices.childNodes.get(itemNum).content.equals(value))
                finalElems.add(elem);
        }
        return finalElems;
    }

    private PMMLElement getFirstWithIndexItem(List<PMMLElement> elements, int itemNum, String value) {
        for (PMMLElement elem : elements) {
            PMMLElement indices = elem.firstNodeWithTag("indexes");
            if (indices != null &&
                    indices.childNodes.size() > itemNum && indices.childNodes.get(itemNum).content.equals(value))
                return elem;
        }
        return null;
    }

    private boolean hasSpecificIndexItem(List<PMMLElement> elements, int itemNum, String value) {
        for (PMMLElement elem : elements) {
            PMMLElement indices = elem.firstNodeWithTag("indexes");
            if (indices != null &&
                    indices.childNodes.size() > itemNum && indices.childNodes.get(itemNum).content.equals(value))
                return true;
        }
        return false;
    }

}
