package com.hpccsystems.pmml2ecl.pmml;

import java.util.Arrays;

import com.hpccsystems.pmml2ecl.Node;

public class PMMLElement extends Node {
    
    public PMMLElement(String nodeType, String rawAttributes, String content) {
        super(nodeType, rawAttributes, content);
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
            if (tagAndAttributes[tagAndAttributes.length-1].endsWith("/")){
                PMMLElement innerElem = new PMMLElement(tagAndAttributes[0], 
                    String.join(" ", Arrays.copyOfRange(tagAndAttributes, 1, tagAndAttributes.length)), 
                    "");
                this.childNodes.add(innerElem);
                cont = cont.substring(endFirstTag + 1);
            } else {
                String endingTag = "</" + tagAndAttributes[0] + ">";
                int endingTagIndex = cont.indexOf(endingTag, endFirstTag);
                PMMLElement innerElem = new PMMLElement(tagAndAttributes[0], 
                    String.join(" ", Arrays.copyOfRange(tagAndAttributes, 1, tagAndAttributes.length)), 
                    cont.substring(endFirstTag + 1, endingTagIndex));
                this.childNodes.add(innerElem);
                cont = cont.substring(endingTagIndex + endingTag.length());
            }
        }
        this.content = cont.trim();
    }

    @Override
    public String toString() {
        String build = "<" + this.nodeType + (this.getRawAttributes().length() > 0 ? " " + this.getRawAttributes() : "") + ">";
        for (int i = 0; i < this.childNodes.size(); i++) {
            build += "\n" + this.childNodes.get(i).toString();
        }
        return build + (content.length() > 0 ? "\n" + content : "") + "\n</" + this.nodeType + ">";
    }

}
