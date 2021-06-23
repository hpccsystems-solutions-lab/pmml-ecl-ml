package com.hpccsystems.pmml2ecl.pmml.operations;

import com.hpccsystems.pmml2ecl.pmml.PMMLElement;

import java.util.ArrayList;
import java.util.HashMap;

public class CommonElements {

    public static PMMLElement createNewComment(String comment) {
        return new PMMLElement("", "", comment, false, true);
    }

    public static PMMLElement emptyElement(String tag) {
        return new PMMLElement(tag, new HashMap<>(), new ArrayList<>(), false);
    }

}
