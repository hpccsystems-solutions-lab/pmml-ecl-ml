package com.hpccsystems.pmml2ecl.ecl.algorithms;

import com.hpccsystems.pmml2ecl.Node;
import com.hpccsystems.pmml2ecl.pmml.PMMLElement;

import java.util.ArrayList;
import java.util.List;

public class LogisticRegression implements Algorithm {

    private PMMLElement rootECL;

    public LogisticRegression(PMMLElement rootECL) {
        this.rootECL = rootECL;
    }

    public PMMLElement getStoredModel() {
        PMMLElement modelRoot = new PMMLElement("PMML",
                "version=\"4.4\" xmlns=\"http://www.dmg.org/PMML-4_4\"", "", false);

        PMMLElement header = new PMMLElement("Header", "", "", true);
        modelRoot.addChild(header);

        PMMLElement model = rootECL.firstNodeWithTag("Dataset");

        List<PMMLElement> rows = new ArrayList<>();
        for (Node node : model.childNodes) {
            PMMLElement idNode = ((PMMLElement) node).firstNodeWithTag("id");
            if (idNode != null) {
                try {
                    Integer value = Integer.parseInt(idNode.content);
                    if (value >= 5) {
                        rows.add(idNode);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        return modelRoot;
    }


}
