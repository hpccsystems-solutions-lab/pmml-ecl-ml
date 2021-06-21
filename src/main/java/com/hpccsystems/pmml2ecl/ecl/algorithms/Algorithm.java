package com.hpccsystems.pmml2ecl.ecl.algorithms;

import com.hpccsystems.pmml2ecl.pmml.PMMLElement;

public interface Algorithm {

    PMMLElement getStoredModel();

    void writeStoredModel() throws Exception;

}
