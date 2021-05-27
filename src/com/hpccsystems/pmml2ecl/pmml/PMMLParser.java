package com.hpccsystems.pmml2ecl.pmml;

import java.io.*;
import com.hpccsystems.pmml2ecl.Node;

import java.util.Scanner;
import java.util.regex.Pattern;

public class PMMLParser {

    private PMMLElement rootNode;

    public PMMLParser(BufferedInputStream buffer) {

    }

    public PMMLParser(String filename) throws Exception {
        Pattern acceptableEndings = Pattern.compile("(\\.(xml|pmml))$", Pattern.CASE_INSENSITIVE);
        if (!acceptableEndings.matcher(filename).find()) {
            throw new Exception("File type not accepted.");
        }
        File file = new File(filename);
        Scanner in = new Scanner(file);
        String fileContents = "";
        while(in.hasNextLine()) {
            fileContents += in.nextLine();
        }
        in.close();
        rootNode = (PMMLElement) new PMMLElement(null, null, fileContents, false).childNodes.get(0);
    }

    public PMMLParser(String fileContents, boolean differentiator) {
        rootNode = (PMMLElement) new PMMLElement(null, null, fileContents, false).childNodes.get(0);
    }

    public PMMLElement getRoot() {
        return rootNode;
    }

}