package com.hpccsystems.pmml2ecl.pmml.algorithms;

import com.hpccsystems.pmml2ecl.ecl.ECLElement;
import com.hpccsystems.pmml2ecl.pmml.PMMLElement;

import java.util.LinkedList;

public interface Algorithm {

    LinkedList<ECLElement> getEclFromModel();

}
