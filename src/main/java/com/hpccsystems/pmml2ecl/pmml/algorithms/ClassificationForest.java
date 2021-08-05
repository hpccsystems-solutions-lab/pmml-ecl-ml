package com.hpccsystems.pmml2ecl.pmml.algorithms;

import com.hpccsystems.pmml2ecl.Node;
import com.hpccsystems.pmml2ecl.ecl.ECLElement;
import com.hpccsystems.pmml2ecl.pmml.PMMLElement;

import java.util.*;

public class ClassificationForest implements Algorithm {

    private PMMLElement fullModel;

    public ClassificationForest(PMMLElement fullModel) {
        this.fullModel = fullModel;
    }

    @Override
    public LinkedList<ECLElement> getEclFromModel() throws Exception {
        LinkedList<ECLElement> modelECL = new LinkedList<>();

        modelECL.add(new ECLElement("IMPORT LearningTrees AS LT;"));
        //IMPORT LT.LT_Types AS Types; ?

        PMMLElement model = fullModel.firstNodeWithTag("Segmentation");
        if (model != null) {
            modelECL.addAll(getSegmentationECL(model));
            return modelECL;
        } else {
            model = fullModel.firstNodeWithTag("TreeModel");
            if (model != null) {
                modelECL.addAll(getTreeModelECL(model));
                return modelECL;
            } else {
                return null;
            }
        }
    }

    private LinkedList<ECLElement> getSegmentationECL(PMMLElement model) {
        LinkedList<ECLElement> segmentationECL = new LinkedList<>();
        List<PMMLElement> segments = model.allNodesWithTag("Segment");

        segmentationECL.add(new ECLElement("model := DATASET([\n    "));

        int counter = 1;
        for (PMMLElement tree : segments) {
            segmentationECL.addAll(getNodesFromTree(tree.firstNodeWithTag("TreeModel"), counter));
            counter++;
        }

        segmentationECL.add(new ECLElement("], ML_Core.Types.Layout_Model2);\n"));

        return segmentationECL;
    }


    private LinkedList<ECLElement> getTreeModelECL(PMMLElement model) {
        LinkedList<ECLElement> segmentationECL = new LinkedList<>();
        //TODO: support single Tree Models. As of now, my ECL to PMML converter always
        //      has a Segmentation so this is not reached by my converted models.
        return null;
    }


    private List<ECLElement> getNodesFromTree(PMMLElement tree, int treeId) {
        LinkedList<ECLElement> nodes = new LinkedList<>();

        Queue<TreeNode> nodesLeft = new LinkedList<>();

        nodesLeft.add(new TreeNode(tree.firstNodeWithTag("Node"), 1, "0"));
        int indexCounter = 1;
        while (nodesLeft.size() > 0) {
            TreeNode currentNode = nodesLeft.remove();
            PMMLElement predicate = currentNode.element.firstNodeWithTag("SimplePredicate");

            String nodeId = currentNode.element.getValue("id");

            for (Node n : currentNode.element.childNodes) {
                PMMLElement p = (PMMLElement) n;
                if (p.getTag().equals("Node")) {
                    nodesLeft.add(new TreeNode(p, currentNode.getNextLevel(), nodeId));
                }
            }
            if (predicate != null) {
                //get split values
            } else {

            }
            System.out.println(treeId + ", " + currentNode.level + ", " + nodeId + ", " + currentNode.parentId);
            //need to add 11 nodes per node
            nodes.add(new ECLElement(treeId + ", " + currentNode.level + ", " + nodeId + ", " + currentNode.parentId));
            indexCounter++;
        }

        return nodes;
    }

    private class TreeNode {

        PMMLElement element;
        int level;
        String parentId;

        TreeNode(PMMLElement element, int level, String parentId) {
            this.element = element;
            this.level = level;
            this.parentId = parentId;
        }

        int getNextLevel() {
            return level + 1;
        }

    }

}
