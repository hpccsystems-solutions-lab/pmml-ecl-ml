package com.hpccsystems.pmml2ecl.ecl.algorithms;

import com.hpccsystems.pmml2ecl.pmml.PMMLElement;

public interface Algorithm {

    String writeStoredModel() throws Exception;
    String writeStoredModel(String absoluteFilePath) throws Exception;

}
