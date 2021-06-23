package com.hpccsystems.pmml2ecl.pmml;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import com.hpccsystems.pmml2ecl.Node;

public class PMMLElement extends Node {

    private boolean selfClosing;
    private boolean comment;

    /**
     * A constructor for a PMMLElement that parses the attributes and content based on the input
     * strings.
     * @param nodeType
     * @param rawAttributes a string that contains all the attribute key/value pairs.
     * @param content a string that contains all of the inner text or inner PMML.
     * @param selfClosing if the element should contain children or not
     */
    public PMMLElement(String nodeType, String rawAttributes, String content, boolean selfClosing) {
        super(nodeType, rawAttributes, content);
        this.selfClosing = selfClosing;
        this.comment = false;
        splitContent();
        splitAttributes();
    }

    /**
     * A constructor for a PMMLElement that parses the attributes and content based on the input
     * strings.
     * @param nodeType
     * @param rawAttributes a string that contains all the attribute key/value pairs.
     * @param content a string that contains all of the inner text or inner PMML.
     * @param selfClosing if the element should contain children or not
     */
    public PMMLElement(String nodeType, String rawAttributes, String content,
                       boolean selfClosing, boolean comment) {
        super(nodeType, rawAttributes, content);
        this.selfClosing = selfClosing;
        this.comment = comment;
        splitContent();
        splitAttributes();
    }

    /**
     * A constructor for a PMMLElement that takes in the final variable pointers rather than constructing them.
     * @param nodeType
     * @param attributes the key/value attribute pairs in a Map.
     * @param childNodes the children nodes in a List.
     * @param selfClosing if the element should contain children or not
     */
    public PMMLElement(String nodeType, Map<String, String> attributes, List<Node> childNodes, boolean selfClosing) {
        super(nodeType, "", "");
        this.attributes = attributes;
        this.childNodes = childNodes;
        this.selfClosing = selfClosing;
        this.comment = false;
        joinMapAttributes();
    }

    public void appendContent(String s) {
        this.content += s;
    }

    public void appendContent(byte s) {
        this.content += s;
    }

    public void setComment() {
        comment = true;
    }

    private void joinMapAttributes() {
        String rawAttribs = "";
        for (String key : this.attributes.keySet()) {
            rawAttribs += key + "=\"" + this.attributes.get(key) + "\" ";
        }
        this.setRawAttributes(rawAttribs.trim());
    }

    private void splitAttributes() {
        String attr = this.getRawAttributes();
        int index = 0;
        while (index >= 0 && index < attr.length()) {
            if (attr.indexOf('=', index) < 0) {
                break;
            }
            String key = attr.substring(index, attr.indexOf('=', index));
            int valStart = attr.indexOf('"', index) >= 0 ? attr.indexOf('"', index) : attr.indexOf('\'', index);
            int valEnd = attr.indexOf('"', valStart + 1) >= 0 ? attr.indexOf('"', valStart + 1) : attr.indexOf('\'', valStart + 1);
            String value = attr.substring(valStart + 1, valEnd);
            this.attributes.put(key, value);
            attr = attr.substring(valEnd+1).trim();
        }

    }

    private void splitContent() {
        String cont = this.getContent().trim();
        int index = cont.indexOf('<');

        if (this.comment) {
            this.content = cont;
            return;
        }
        while (index >= 0 && index < cont.length()) {
            int endFirstTag = cont.indexOf('>', index);
            String[] tagAndAttributes = cont.substring(index + 1, endFirstTag).split(" ");
            String tag = tagAndAttributes[0];
            if (tagAndAttributes[tagAndAttributes.length - 1].endsWith("/")) {
                //If it is a self-closing tag..
                PMMLElement innerElem = new PMMLElement(tag,
                        cont.substring(index + 1, endFirstTag - 1).replaceFirst(tag, "").trim(),
                        "", true);
                this.childNodes.add(innerElem);
                cont = cont.substring(endFirstTag + 1).trim();
            } else if (checkComment(cont.substring(index + 1))) {
                int beginComment = cont.indexOf("<!--");
                int endComment = cont.indexOf("-->");
                String comment = cont.substring(beginComment + 4, endComment);
                PMMLElement innerElem = new PMMLElement("", "", comment, false, true);
                this.childNodes.add(innerElem);
                cont = cont.substring(endComment + 3);
            } else {
                //if it actually has a closing tag.
                String endingTag = "</" + tag + ">";
                int endingTagIndex = cont.indexOf(endingTag, endFirstTag);
                //This is where the recursion happens.
                PMMLElement innerElem = new PMMLElement(tag,
                        cont.substring(index + 1, endFirstTag).replaceFirst(tag, "").trim(),
                        cont.substring(endFirstTag + 1, endingTagIndex).trim(), false);
                this.childNodes.add(innerElem);
                cont = cont.substring(endingTagIndex + endingTag.length());
            }
        }
        this.content = cont.trim();
    }

    /**
     * returns true if found beginning comment, false is not found
     * @param check
     * @return
     */
    private boolean checkComment(String check) {
        Pattern commentCheck = Pattern.compile("^(!--)", Pattern.CASE_INSENSITIVE);
        return commentCheck.matcher(check).find();
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
     * Gets the first PMMLElement with a given tag. Not case sensitive.
     * @param name The name of the tag
     * @return the first PMMLElement with given tag (including children nodes)
     */
    public PMMLElement firstNodeWithTag(String name) {
        if (this.nodeType.toLowerCase().equals(name.toLowerCase())) {
            return this;
        } else {
            for (int i = 0; i < this.childNodes.size(); i++) {
                PMMLElement search = ((PMMLElement) this.childNodes.get(i)).firstNodeWithTag(name);
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
     *
     * @param key
     * @return
     */
    public String getValue(String key) {
        return attributes.get(key);
    }

    /**
     * Overriden toString() that gets full layout of PMMLElement recursively.
     */
    @Override
    public String toString() {
        if (this.selfClosing) {
            return "<" + this.nodeType + (this.getRawAttributes().length() > 0 ? " " + this.getRawAttributes() : "") + "/>";
        }
        if (this.comment) {
            return "<!-- " + this.content + " -->";
        }
        if (this.childNodes.size() > 0) {
            String build = "<" + this.nodeType + (this.getRawAttributes().length() > 0 ? " " + this.getRawAttributes() : "") + ">";
            for (int i = 0; i < this.childNodes.size(); i++) {
                build += ("\n" + this.childNodes.get(i).toString()).replace("\n", "\n    ");
            }
            return build + (content.length() > 0 ? "\n" + content : "") + "\n</" + this.nodeType + ">";
        } else {
            return  "<" + this.nodeType + (this.getRawAttributes().length() > 0 ? " " + this.getRawAttributes() : "") + ">" +
                    content + "</" + this.nodeType + ">";
        }
    }

    /**
     * Writes the current PMMLElement to /output/PMMLOutput.xml
     * @throws Exception
     */
    public void writeToFile() throws Exception {
        File file = new File(System.getProperty("user.dir") + "/output/PMMLOutput.xml");
        //TODO: check for folder
        if (!file.exists()) {
           file.createNewFile();
        } 
        FileWriter fw = new FileWriter(file.getAbsoluteFile());
        BufferedWriter bw = new BufferedWriter(fw);
        bw.write(this.toString());
        bw.close();
    }

    /**
     * Writes the current PMMLElement to /output/PMMLOutput.xml
     * @throws Exception
     */
    public void writeToFile(String identifier) throws Exception {
        File outputDir = new File(System.getProperty("user.dir") + "/output");
        outputDir.mkdirs();
        File file = new File(System.getProperty("user.dir") + "/output/PMMLOutput-" + identifier.trim() + ".xml");
        //TODO: check for folder
        if (!file.exists()) {
            file.createNewFile();
        }
        FileWriter fw = new FileWriter(file.getAbsoluteFile());
        BufferedWriter bw = new BufferedWriter(fw);
        bw.write(this.toString());
        bw.close();
    }

    /**
     * Overriden method that doesn't allow for self closing tags to have children
     */
    @Override
    public void addChild(Node node) {
        if (!selfClosing) {
            super.addChild(node);
        } else {
            return;
        }
    }

    /**
     * Overriden method that doesn't allow for self closing tags to have children
     */
    @Override
    public void addChildren(List<Node> nodes) {
        if (!selfClosing) {
            super.addChildren(nodes);
        } else {
            return;
        }
    }

}
