package com.hpccsystems.pmml2ecl.pmml.algorithms;

import com.hpccsystems.pmml2ecl.ecl.ECLElement;
import com.hpccsystems.pmml2ecl.pmml.PMMLElement;

import java.util.LinkedList;

public class LogisticRegression implements Algorithm {

    private PMMLElement model;

    public LogisticRegression(PMMLElement model) {
        this.model = model;
    }

    @Override
    public LinkedList<ECLElement> getEclFromModel() {
        LinkedList<ECLElement> modelECL = new LinkedList<>();
        PMMLElement schema = model.firstNodeWithTag("MiningSchema");
        PMMLElement table = model.firstNodeWithTag("RegressionTable");

        modelECL.add(new ECLElement("IMPORT LogisticRegression as LR;"));

        return modelECL;
    }
}
