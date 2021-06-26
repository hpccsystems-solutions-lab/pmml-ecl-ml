package com.hpccsystems.pmml2ecl.ecl;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.LinkedList;
import java.util.Scanner;
import java.util.regex.Pattern;

import com.hpccsystems.pmml2ecl.ecl.algorithms.LinearRegression;
import com.hpccsystems.pmml2ecl.ecl.algorithms.LogisticRegression;
import com.hpccsystems.pmml2ecl.pmml.PMMLElement;

public class ECLParser {

    private PMMLElement rootNode;

    /**
     * Parses the result XML from compiling the .ecl code.
     * @param xmlFilePath the absolute path of the result xml
     * @throws Exception
     */
    public ECLParser(String xmlFilePath) throws Exception {
        Pattern acceptableEndings = Pattern.compile("(\\.(xml))$", Pattern.CASE_INSENSITIVE);
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
    }

    private void convertToPMML() throws Exception {
        PMMLElement type = this.rootNode.firstNodeWithAttribute("name", "Result 1");
        PMMLElement model = this.rootNode.firstNodeWithAttribute("name", "Result 2");
        if (type != null && model != null) {
            switch (type.childNodes.get(0).childNodes.get(0).content) {
                case "LinearRegression":
                    new LinearRegression(model).writeStoredModel();
                    break;
                case "LogisticRegression":
                    new LogisticRegression(model).writeStoredModel();
                    break;
                default:
                    throw new Exception("Model type not well defined or outputted correctly.");
            }
        } else {
            throw new Exception("The model type or model was not defined properly or outputted in .ecl file.");
        }
    }

    /**
     * Writes the current parsed result XML to /output/PMMLOutput.xml
     * @throws Exception
     */
    public void writeToOutput() throws Exception {
        this.rootNode.writeToFile();
    }

    /**
     * Writes the elements to /output/ECLOutput.ecl for consumption
     * @param eclElements List of eclElements to write
     * @throws Exception
     */
    public static void writeToFile(LinkedList<ECLElement> eclElements) throws Exception {
        File directory = new File(System.getProperty("user.dir") + "/output");
        File file = new File(System.getProperty("user.dir") + "/output/ECLOutput.ecl");
        directory.mkdirs();
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
