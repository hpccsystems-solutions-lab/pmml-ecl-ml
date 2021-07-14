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
        PMMLElement model = rootECL.firstNodeWithTag("Dataset");

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
        HashMap<String, TreeNode> nodeMap = new HashMap<>();
        HashSet<String> dependents = new HashSet<>();
        HashSet<String> scores = new HashSet<>();
        HashSet<String> segments = new HashSet<>();
        while (hasSpecificIndexItem(elementsToWorkOn, 1, Integer.toString(counter))) {
            List<PMMLElement> listOfCounter =
                    getAllWithSpecificIndexItem(elementsToWorkOn, 1, Integer.toString(counter));
            String[] nodeContent = new String[11];
            for (int i = 0; i < nodeContent.length; i++) {
                Node pmmlNode = getFirstWithIndexItem(listOfCounter, 2, Integer.toString(i + 1)).firstNodeWithTag("value");
                nodeContent[i] = pmmlNode != null ? pmmlNode.content : null;
            }

            nodeContent[2] = nodeContent[2].replaceAll("\\.0", "");
            nodeContent[3] = nodeContent[3].replaceAll("\\.0", "");

            System.out.println(Arrays.toString(nodeContent));

            segments.add(nodeContent[0]);

            if (nodeContent[6].equals("0.0")) {
                System.out.println(Arrays.toString(nodeContent));
                Map<String, String> attr = new HashMap<>();
                attr.put("id", nodeContent[2]);
                attr.put("score", nodeContent[8]);

                PMMLElement node = new PMMLElement("Node", attr, new ArrayList<>(), false);
                nodeMap.put(nodeContent[2], new TreeNode(nodeContent[3], nodeContent[0], node));
            } else {
                Map<String, String> attr = new HashMap<>();
                attr.put("id", nodeContent[2]);

                PMMLElement node = new PMMLElement("Node", attr, new ArrayList<>(), false);

                Map<String, String> predicateAttr = new HashMap<>();
                predicateAttr.put("field", nodeContent[8]);
                predicateAttr.put("value", nodeContent[6]);
                //1.0 is left, 0.0 is right
                predicateAttr.put("operator", nodeContent[5].equals("1.0") ? "lessOrEqual" : "greaterThan");

                PMMLElement predicate = new PMMLElement("SimplePredicate", predicateAttr, new ArrayList<>(), true);
                node.addChild(predicate);
                nodeMap.put(nodeContent[2], new TreeNode(nodeContent[3], nodeContent[0], node));
            }


            counter++;
        }

        HashMap<String, PMMLElement> segmentMap = new HashMap<>();

        for (String nodeId : nodeMap.keySet()) {
            TreeNode currNode = nodeMap.get(nodeId);
            TreeNode parentNode = nodeMap.get(currNode.parentId);
            if (currNode.treeId.equals(parentNode.treeId) &&
                    !currNode.node.getValue("id").equals(parentNode.node.getValue("id"))) {
                parentNode.node.addChild(currNode.node);
            }
        }
        for (String nodeId : nodeMap.keySet()) {
            TreeNode currNode = nodeMap.get(nodeId);
            if (currNode.node.childNodes.size() == 0) {
                currNode.node.setSelfClosing(true);
            }
        }

        baseNode.addChild(nodeMap.get("1").node);

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

    private class TreeNode {

        String parentId;
        String treeId;
        PMMLElement node;

        TreeNode(String parentId, String treeId, PMMLElement node) {
            this.parentId = parentId;
            this.node = node;
        }

    }

}
