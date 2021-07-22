package com.hpccsystems.pmml2ecl.ecl.algorithms;

import com.hpccsystems.pmml2ecl.Node;
import com.hpccsystems.pmml2ecl.pmml.PMMLElement;
import com.hpccsystems.pmml2ecl.pmml.operations.CommonElements;
import com.hpccsystems.pmml2ecl.pmml.operations.ElementFinder;
import com.hpccsystems.pmml2ecl.pmml.operations.FileNames;

import java.lang.reflect.Array;
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

        //TODO: Add children to Base Node. RECURSIVELY?
        //      Iterate over all "Items" and then create nodes for them.
        //      Do it destructively to cut down on time? Eh do it regularly first

        HashMap<String, Segment> segments = new HashMap<>();
        int idCounter = 1;
        String segmentId = "1";
        for (int i = 0; i < elementsToWorkOn.size();) {
            String[] nodeContent = new String[11];
            for (int j = 0; j < 11 && i < elementsToWorkOn.size(); j++) {
                PMMLElement elem = elementsToWorkOn.get(i);
                nodeContent[j] = elem.childNodes.get(1).content;
                i++;
            }
            for (int x = 0; x < 4; x++) {
                nodeContent[x] = cleanDouble(nodeContent[x]);
            }
            if (!segments.containsKey(nodeContent[0])) {
                Segment seg = new Segment(nodeContent[0]);
                segments.put(nodeContent[0], seg);
            }
            if (!segmentId.equals(nodeContent[0])) {
                segmentId = nodeContent[0];
                idCounter = 1;
            }
            Segment seg = segments.get(nodeContent[0]);
            if (!seg.levels.containsKey(nodeContent[1])) {
                Level lev = new Level(nodeContent[1]);
                seg.levels.put(nodeContent[1], lev);
            }
            Level lev = seg.levels.get(nodeContent[1]);

            if (nodeContent[5].equals("0.0")) {
                Map<String, String> attr = new HashMap<>();
                attr.put("id", Integer.toString(idCounter));
                attr.put("score", nodeContent[8]);

                PMMLElement pmmlNode = new PMMLElement("Node", attr, new ArrayList<>(), false);
                TreeNode node = new TreeNode(nodeContent[2], nodeContent[3], pmmlNode);
                lev.addNode(node);
            } else {
                Map<String, String> attr = new HashMap<>();
                attr.put("id", Integer.toString(idCounter));

                PMMLElement pmmlNode = new PMMLElement("Node", attr, new ArrayList<>(), false);

                Map<String, String> predicateAttr = new HashMap<>();
                predicateAttr.put("field", nodeContent[5]);
                predicateAttr.put("value", nodeContent[6]);
                //1.0 is left, 0.0 is right
                predicateAttr.put("operator", nodeContent[5].equals("1.0") ? "lessOrEqual" : "greaterThan");

                PMMLElement predicate = new PMMLElement("SimplePredicate", predicateAttr, new ArrayList<>(), true);
                pmmlNode.addChild(predicate);
                TreeNode node = new TreeNode(nodeContent[2], nodeContent[3], pmmlNode);
                lev.addNode(node);
            }
            idCounter++;
        }

        int numSegments = segments.size();
        for (int s = 1; s <= numSegments; s++) {
            Segment segment = segments.get(Integer.toString(s));

            Map<String, Level> levels = segment.levels;
            int numLevels = levels.size();
            for (int i = 1; i <= numLevels; i++) {
                Level level = levels.get(Integer.toString(i));

                Map<String, TreeNode> nodes = level.nodes;
                for (String nodeKey : nodes.keySet()) {
                    if (i > 1) {
                        TreeNode node = nodes.get(nodeKey);
                        Level parentLevel = levels.get(Integer.toString(i - 1));
                        parentLevel.nodes.get(node.parentId).node.addChild(node.node);
                        parentLevel.nodes.get(node.parentId).childNodes.add(node);
                    }

                }

            }

//            boolean remove;
//            do {
//                remove = false;
//                for (int i = 1; !remove && i <= numLevels; i++) {
//                    Level level = levels.get(Integer.toString(i));
//                    Map<String, TreeNode> nodes = level.nodes;
//                    for (String nodeKey : nodes.keySet()) {
//                        TreeNode node = nodes.get(nodeKey);
//                        if (i != 1 && node.node.childNodes.size() == 2) {
//                            remove = true;
//                            break;
//                        }
//                    }
//                }
                for (int i = 1; i <= numLevels; i++) {
                    Level level = levels.get(Integer.toString(i));

                    Map<String, TreeNode> nodes = level.nodes;
                    for (String nodeKey : nodes.keySet()) {
                        TreeNode node = nodes.get(nodeKey);
                        if (node.node.childNodes.size() == 0) {
                            node.node.setSelfClosing(true);
                        }
                        if (i != 1 && node.node.childNodes.size() == 2) {
                            Level parentLevel = levels.get(Integer.toString(i - 1));
                            Level childLevel = levels.get(Integer.toString(i + 1));
                            TreeNode parent = parentLevel.nodes.get(node.parentId);
                            Node childNode = node.node.childNodes.get(1);
                            parent.node.childNodes.add(childNode);
                            parent.node.childNodes.remove(node.node);
                            parent.childNodes.addAll(node.childNodes);
                            //remove current from levels, move child up a level
                            nodes.remove(nodeKey);
                        }
                    }

                }
//            } while (remove);
            idCounter = 1;
            Queue<TreeNode> elementsToRename = new LinkedList<>();
            elementsToRename.add(levels.get("1").nodes.get("1"));
            while (elementsToRename.size() > 0) {
                TreeNode node = elementsToRename.remove();
                node.node.attributes.put("id", Integer.toString(idCounter));
                elementsToRename.addAll(node.childNodes);
                idCounter++;
            }

            Map<String, String> segAttr = new HashMap<>();
            segAttr.put("id", segment.segmentId);
            PMMLElement segmentNode = new PMMLElement("Segment", segAttr, new ArrayList<>(), false);

            Map<String, String> treeParams = new HashMap<>();
            treeParams.put("modelName", "randomForest_Model");
            treeParams.put("functionName", "classification");
            treeParams.put("algorithmName", "randomForest");
            PMMLElement treeModel = new PMMLElement("TreeModel", treeParams, new ArrayList<>(), false);

            treeModel.addChild(segment.levels.get("1").nodes.get("1").node);
            segmentNode.addChild(treeModel);
            finalModel.addChild(segmentNode);
        }

        return finalModel;
    }

    private String cleanDouble(String doub) {
        return doub.replaceAll("\\.0$", "");
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

        String nodeId;
        String parentId;
        PMMLElement node;
        List<TreeNode> childNodes;

        TreeNode(String nodeId, String parentId, PMMLElement node) {
            this.parentId = parentId;
            this.nodeId = nodeId;
            this.node = node;
            childNodes = new ArrayList<>();
        }

    }

    private class Segment {

        Map<String, Level> levels;
        public String segmentId;

        public Segment(String segmentId) {
            this.segmentId = segmentId;
            this.levels = new HashMap<>();
        }

        public void addLevel(Level l) {
            levels.put(l.levelNum, l);
        }

        public boolean hasLevel(String l) {
            return levels.containsKey(l);
        }

    }

    private class Level {

        Map<String, TreeNode> nodes;
        public String levelNum;
        private int capacity;

        public Level(String levelNum) {
            this.levelNum = levelNum;
            this.capacity = (int) Math.pow(2.0, Double.parseDouble(levelNum) - 1);
            nodes = new HashMap<>();
        }

        public void addNode(TreeNode n) {
            if (Double.parseDouble(n.nodeId) <= capacity) {
                nodes.put(n.nodeId, n);
            }
        }

    }

}
