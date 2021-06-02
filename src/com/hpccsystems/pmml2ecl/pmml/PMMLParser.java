package com.hpccsystems.pmml2ecl.pmml;

import java.io.*;
import com.hpccsystems.pmml2ecl.Node;

import java.util.Scanner;
import java.util.regex.Pattern;

public class PMMLParser {

    private PMMLElement rootNode;

    public PMMLParser(BufferedInputStream buffer) {

    }

    /**
     * Creates an instance of a PMMLElement given a file path. Will throw errors for non .xml or .pmml files or
     * if there is more than one root element.
     * @param filepath
     * @throws Exception 
     */
    public PMMLParser(String filepath) throws Exception {
        Pattern acceptableEndings = Pattern.compile("(\\.(xml|pmml))$", Pattern.CASE_INSENSITIVE);
        if (!acceptableEndings.matcher(filepath).find()) {
            throw new Exception("File type not accepted.");
        }
        File file = new File(filepath);
        Scanner in = new Scanner(file);
        String fileContents = "";
        while(in.hasNextLine()) {
            fileContents += in.nextLine().trim();
        }
        in.close();
        rootNode = new PMMLElement(null, null, fileContents, false);
        if (rootNode.childNodes.size() == 1) {
            rootNode = (PMMLElement) rootNode.childNodes.get(0);
        } else {
            throw new Exception("More than one root element.");
        }
    }

    public PMMLParser(String fileContents, boolean differentiator) {
        rootNode = (PMMLElement) new PMMLElement(null, null, fileContents, false).childNodes.get(0);
    }

    public PMMLElement getRoot() {
        return rootNode;
    }

}