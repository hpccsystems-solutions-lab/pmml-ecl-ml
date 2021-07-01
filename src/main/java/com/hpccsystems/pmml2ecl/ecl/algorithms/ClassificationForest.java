package com.hpccsystems.pmml2ecl.ecl.algorithms;

import com.hpccsystems.pmml2ecl.pmml.PMMLElement;

import java.util.LinkedList;
import java.util.List;

public class ClassificationForest implements Algorithm{

    @Override
    public void writeStoredModel() throws Exception {

    }

    @Override
    public void writeStoredModel(String absoluteFilePath) throws Exception {

    }

    private PMMLElement getStoredModel(List<PMMLElement> elementsToWorkOn) {
        PMMLElement finalModel = new PMMLElement("PMML",
                "version=\"4.4\" xmlns=\"http://www.dmg.org/PMML-4_4\"", "", false);



        return finalModel;
    }

}
