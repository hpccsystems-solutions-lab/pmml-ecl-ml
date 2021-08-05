package com.hpccsystems.pmml2ecl.pmml;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import com.hpccsystems.pmml2ecl.Node;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Suspendable;

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
                tag = tag.replaceAll("/", "");
                String attr = cont.substring(index + tag.length() + 1, endFirstTag - 1).replaceFirst(tag, "").trim();
                PMMLElement innerElem = new PMMLElement(tag,
                        attr,
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
                //if it SHOULD have a closing tag.
                String endingTag = "</" + tag + ">";
                int endingTagIndex = findEnd(tag, cont, endFirstTag);

                String attributes = cont.substring(index + 1, endFirstTag).replaceFirst(tag, "").trim();
                String innerContent = cont.substring(endFirstTag + 1, endingTagIndex).trim();
                cont = cont.substring(endingTagIndex + endingTag.length());

                PMMLElement innerElem = new PMMLElement(tag, attributes, innerContent, false);
                this.childNodes.add(innerElem);
            }
        }
        this.content = cont.trim();
    }

    private int findEnd(String tag, String content, int endFirstTag) {

        String endingTag = "</" + tag + ">";
        String nextTag = "<" + tag;

        int startSearch = endFirstTag;
        int lastSearch = startSearch;
        int endingTagIndex = content.indexOf(endingTag, startSearch);
        int nextOpen = content.indexOf(nextTag, startSearch);
        int numOpen = 1;
        while (numOpen > 0) {
            //find index of next open tag or closed tag.
            if (endingTagIndex < 0) {
                startSearch = lastSearch;
                break;
            } else if (nextOpen < 0) {
                startSearch = lastSearch;
                break;
            } else if (nextOpen < endingTagIndex) {
                int startCheck = content.indexOf("<Node", startSearch + 1);
                int getClose = -1;
                int getSelfClose = -1;
                boolean foundOpen = false;
                do {
                    if (startCheck < 0) {
                        break;
                    }
                    getClose = content.indexOf('>', startCheck);
                    getSelfClose = content.indexOf("/>", startCheck);
                    if (getSelfClose == -1 && getClose != -1) {
                        //found close but not self close
                        startSearch = startCheck;
                        numOpen++;
                        break;
                    } else if (getSelfClose < getClose) {
                        //found self closing.
                        startCheck = content.indexOf("<Node", startCheck + 1);
                    } else if (getSelfClose > getClose) {
                        foundOpen = true;
                        startSearch = startCheck + ("<" + tag).length();
                        numOpen++;
                        break;
                    }
                } while (getClose != -1);

                if (!foundOpen) {
                    startSearch = endingTagIndex;
                }

            } else if (nextOpen > endingTagIndex) {
                if (numOpen == 1) {
                    numOpen--;
                } else {
                    lastSearch = startSearch;
                    startSearch = endingTagIndex + endingTag.length();
                    numOpen--;
                }
            }
            int nextEndingIn = content.indexOf(endingTag, startSearch);
            endingTagIndex = nextEndingIn > 0 ? nextEndingIn : endingTagIndex;
            nextOpen = content.indexOf(nextTag, startSearch);
        }
        int nextEndingIn = content.indexOf(endingTag, startSearch);
        endingTagIndex = nextEndingIn > 0 ? nextEndingIn : endingTagIndex;
        if (endingTagIndex == -1) {
            System.out.println(content);
            return content.indexOf(endingTag, endFirstTag);
        }
        return endingTagIndex;
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

    public void setSelfClosing(boolean selfClosing) {
        this.selfClosing = selfClosing;
    }

    public boolean hasChildren() {
        return this.childNodes.size() > 0;
    }

    public String getTag() {
        return nodeType;
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

    public List<PMMLElement> allNodesWithTag(String tag) {
        List<PMMLElement> allNodes = new ArrayList<>();
        if (this.nodeType.equals(tag)) {
            allNodes.add(this);
        }
        for (int i = 0; i < this.childNodes.size(); i++) {
            List<PMMLElement> search = ((PMMLElement) this.childNodes.get(i)).allNodesWithTag(tag);
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
    public void writeToFile(String identifier, int number) throws Exception {
        File outputDir = new File(System.getProperty("user.dir") + "/output");
        outputDir.mkdirs();
        File file = new File(System.getProperty("user.dir") + "/output/PMMLOutput-" + identifier.trim() + number + ".xml");
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
    public void writeToFile(String absoluteFilePath) throws Exception {
        String direct = absoluteFilePath.substring(0, absoluteFilePath.lastIndexOf("/"));
        File outputDir = new File(direct);
        outputDir.mkdirs();
        File file = new File(absoluteFilePath);
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
