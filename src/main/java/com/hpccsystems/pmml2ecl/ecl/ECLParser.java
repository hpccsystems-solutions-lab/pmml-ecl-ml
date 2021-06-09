package com.hpccsystems.pmml2ecl.ecl;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.LinkedList;
import java.util.Scanner;
import java.util.regex.Pattern;

import com.hpccsystems.pmml2ecl.pmml.PMMLElement;

public class ECLParser {

    private LinkedList<ECLElement> allElems;
    private PMMLElement rootNode;

    public ECLParser(String xmlFilePath) throws Exception {
        Pattern acceptableEndings = Pattern.compile("(\\.(xml|pmml))$", Pattern.CASE_INSENSITIVE);
        if (!acceptableEndings.matcher(xmlFilePath).find()) {
            throw new Exception("File type not accepted.");
        }
        File file = new File(xmlFilePath);
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
        convertToPMML();
        this.rootNode.writeToFile();
    }
    
    public ECLParser(String fileContents, boolean diff) {
        allElems = new LinkedList<>();
        String cont = fileContents;
        int index = 0;
        while (index >= 0 && cont.length() > 0) {
            index = cont.indexOf(';', 0);
            allElems.add(new ECLElement(cont.substring(0, index).replaceAll("(//).+\n", "").trim()));
            cont = cont.substring(index + 1);
        }
    }

    public LinkedList<ECLElement> getElems() {
        return allElems;
    }

    private void convertToPMML() {
        this.rootNode = new XMLMLConverter(this.rootNode).toLinearRegression();
    }

    public static void writeToFile(LinkedList<ECLElement> eclElements) throws Exception {
        File file = new File(System.getProperty("user.dir") + "/output/ECLOutput.ecl");
        //TODO: check for folder
        if (!file.exists()) {
            file.createNewFile();
        }
        FileWriter fw = new FileWriter(file.getAbsoluteFile());
        BufferedWriter bw = new BufferedWriter(fw);
        for (ECLElement ecl : eclElements) {
            bw.write(ecl.toString() + "\n");
        }
        bw.close();
    }

}
