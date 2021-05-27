package com.hpccsystems.pmml2ecl.pmml;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.hpccsystems.pmml2ecl.Node;

public class PMMLElement extends Node {

    private boolean selfClosing;
    
    public PMMLElement(String nodeType, String rawAttributes, String content, boolean selfClosing) {
        super(nodeType, rawAttributes, content);
        this.selfClosing = selfClosing;
        splitContent();
        splitAttributes();
    }

    public void appendContent(String s) {
        this.content += s;
    }

    public void appendContent(byte s) {
        this.content += s;
    }

    private void splitAttributes() {
        String attr = this.getRawAttributes();
        int index = 0;
        while (index >= 0 && index < attr.length()) {
            if (attr.indexOf('=', index) < 0) {
                break;
            }
            String key = attr.substring(index, attr.indexOf('=', index));
            int valStart = attr.indexOf('"', index);
            int valEnd = attr.indexOf('"', valStart + 1);
            String value = attr.substring(valStart + 1, valEnd);
            this.attributes.put(key, value);
            attr = attr.substring(valEnd+1).trim();
        }

    }

    private void splitContent() {
        String cont = this.getContent();
        int index = cont.indexOf('<', 0);
        while (index >= 0 && index < cont.length()) {
            int endFirstTag = cont.indexOf('>', index);
            String[] tagAndAttributes = cont.substring(index+1, endFirstTag).split(" ");
            String tag = tagAndAttributes[0];
            if (tagAndAttributes[tagAndAttributes.length-1].endsWith("/")){
                PMMLElement innerElem = new PMMLElement(tag, 
                    cont.substring(index+1, endFirstTag - 1).replaceFirst(tag, "").trim(), 
                    "", true);
                this.childNodes.add(innerElem);
                cont = cont.substring(endFirstTag + 1);
            } else {
                String endingTag = "</" + tag + ">";
                int endingTagIndex = cont.indexOf(endingTag, endFirstTag);
                PMMLElement innerElem = new PMMLElement(tag, 
                    cont.substring(index+1, endFirstTag).replaceFirst(tag, "").trim(), 
                    cont.substring(endFirstTag + 1, endingTagIndex), false);
                this.childNodes.add(innerElem);
                cont = cont.substring(endingTagIndex + endingTag.length());
            }
        }
        this.content = cont.trim();
    }

    /**
     * Function that finds the first PMMLElement given a specific attribute pair
     * @param key The key of the attribute to look for
     * @param value Exact match of attribute value to search for
     * @return The first PMMLElement (and child nodes) that matches the criteria
     */
    public PMMLElement firstNodeWithAttribute(String key, String value) {
        if (this.attributes.containsKey(key) && this.attributes.get(key).equals(value)) {
            return this;
        } else {
            for (int i = 0; i < this.childNodes.size(); i++) {
                PMMLElement search = ((PMMLElement) this.childNodes.get(i)).firstNodeWithAttribute(key, value);
                if (search != null) {
                    return search;
                }
            }
        }
        return null;
    }

    /**
     * Gets the first PMMLElement given a specific attribute key
     * @param key They key of the attribute to look for
     * @return the first PMMLElement (containing children nodes) 
     */
    public PMMLElement firstNodeWithKey(String key) {
        if (this.attributes.containsKey(key)) {
            return this;
        } else {
            for (int i = 0; i < this.childNodes.size(); i++) {
                PMMLElement search = ((PMMLElement) this.childNodes.get(i)).firstNodeWithKey(key);
                if (search != null) {
                    return search;
                }
            }
        }
        return null;
    }

    /**
     * Gets all PMMLElements with a specific attribute pair.
     * @param key attribute key to search for
     * @param value attribute value to search for
     * @return List of PMMLElements (containing children nodes)
     */
    public List<PMMLElement> allNodesMatchingAttribute(String key, String value) {
        List<PMMLElement> allNodes = new ArrayList<>();
        if (this.attributes.containsKey(key) && this.attributes.get(key).equals(value)) {
            allNodes.add(this);
        }
        for (int i = 0; i < this.childNodes.size(); i++) {
            List<PMMLElement> search = ((PMMLElement) this.childNodes.get(i)).allNodesMatchingAttribute(key, value);
            allNodes.addAll(search);
        }
        return allNodes;
    }

    /**
     * Overriden toString() that gets full layout of PMMLElement recursively.
     */
    @Override
    public String toString() {
        if (this.selfClosing) {
            return "<" + this.nodeType + (this.getRawAttributes().length() > 0 ? " " + this.getRawAttributes() : "") + "/>";
        }
        String build = "<" + this.nodeType + (this.getRawAttributes().length() > 0 ? " " + this.getRawAttributes() : "") + ">";
        for (int i = 0; i < this.childNodes.size(); i++) {
            build += ("\n" + this.childNodes.get(i).toString()).replace("\n", "\n   ");
        }
        return build + (content.length() > 0 ? "\n" + content : "") + "\n</" + this.nodeType + ">";
    }

}
