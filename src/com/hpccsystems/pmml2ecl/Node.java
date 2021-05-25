package com.hpccsystems.pmml2ecl;

import java.util.Map;

public interface Node {

    String nodeType;
    Node[] childNodes;
    Map<String, String> attributes;
    String value;

    void addChild();
    
    
}
