package com.hpccsystems.pmml2ecl.ecl.algorithms;

import com.hpccsystems.pmml2ecl.pmml.PMMLElement;

public class LogisticRegression implements Algorithm {

    private PMMLElement rootECL;

    public LogisticRegression(PMMLElement rootECL) {
        this.rootECL = rootECL;
    }

    public PMMLElement getStoredModel() {
        PMMLElement modelRoot = new PMMLElement("PMML",
                "version=\"4.4\" xmlns=\"http://www.dmg.org/PMML-4_4\"", "", false);
        return modelRoot;
    }

}
