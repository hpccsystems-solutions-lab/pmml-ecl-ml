package com.hpccsystems.pmml2ecl.ecl.algorithms;

import com.hpccsystems.pmml2ecl.pmml.PMMLElement;

public interface Algorithm {

    void writeStoredModel() throws Exception;
    void writeStoredModel(String absoluteFilePath) throws Exception;

}
