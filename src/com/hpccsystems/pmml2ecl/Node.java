package com.hpccsystems.pmml2ecl;

import java.util.*;

public class Node {

    protected String nodeType;
    public String content;
    private String rawAttributes;
    public List<Node> childNodes;
    public Map<String, String> attributes;

    public Node(String nodeType, String rawAttributes, String content) {
        this.nodeType = nodeType != null ? nodeType : "";
        this.rawAttributes = rawAttributes != null ? rawAttributes : "";
        this.content = content;
        this.childNodes = new ArrayList<>();
        this.attributes = new HashMap<>();
    }

    public void addChild(Node node) {
        childNodes.add(node);
    };

    public String getType() {
        return this.nodeType;
    }

    public String getContent() {
        return this.content;
    }

    public String getRawAttributes() {
        return this.rawAttributes;
    }
    
}
