package com.hpccsystems.pmml2ecl;

import java.util.LinkedList;
import com.hpccsystems.pmml2ecl.ecl.ECLElement;
import com.hpccsystems.pmml2ecl.ecl.ECLParser;
import com.hpccsystems.pmml2ecl.pmml.PMMLElement;
import com.hpccsystems.pmml2ecl.pmml.PMMLParser;
import com.hpccsystems.pmml2ecl.pmml.algorithms.ClassificationForest;
import com.hpccsystems.pmml2ecl.pmml.algorithms.LinearRegression;
import com.hpccsystems.pmml2ecl.pmml.algorithms.LogisticRegression;

public class PMMLConverter {

    private LinkedList<ECLElement> ecl;

    /**
     * Intakes a file path for a .xml/pmml file to convert into a .ecl file
     * with the corresponding ML model as a variable. The final file resides in /output.
     * @param absoluteFilePath the path of the .xml/.pmml file to convert.
     * @throws Exception
     */
    public PMMLConverter(String absoluteFilePath) throws Exception {
        PMMLParser parser = new PMMLParser(absoluteFilePath);
        PMMLElement root = parser.getRoot();
        PMMLElement model = root.firstNodeWithKey("algorithmName");
        String functionName = model.getValue("algorithmName");
        ecl = new LinkedList<>();
        ecl.add(new ECLElement("IMPORT ML_Core;"));
        ecl.add(new ECLElement("IMPORT ML_Core.Types;"));
        ecl.add(new ECLElement("IMPORT ML_Core.ModelOps2 as ModelOps2;"));
        switch (functionName) {
            case "LinearRegression":
                ecl.addAll(new LinearRegression(model).getEclFromModel());
                break;
            case "LogisticRegression":
                ecl.addAll(new LogisticRegression(model).getEclFromModel());
                break;
            case "randomForest":
                ecl.addAll(new ClassificationForest(root.firstNodeWithTag("Segmentation")).getEclFromModel());
                break;
            default:
                break;
        }
        ECLParser.writeToFile(ecl);
    }

    public PMMLConverter(String absoluteFilePath, String outputPath) throws Exception {
        PMMLParser parser = new PMMLParser(absoluteFilePath);
        PMMLElement root = parser.getRoot();
        PMMLElement model = root.firstNodeWithKey("algorithmName");
        String functionName = model.getValue("algorithmName");
        ecl = new LinkedList<>();
        ecl.add(new ECLElement("IMPORT ML_Core;"));
        ecl.add(new ECLElement("IMPORT ML_Core.Types;"));
        ecl.add(new ECLElement("IMPORT ML_Core.ModelOps2 as ModelOps2;"));
        switch (functionName) {
            case "LinearRegression":
                ecl.addAll(new LinearRegression(model).getEclFromModel());
                break;
            case "LogisticRegression":
                ecl.addAll(new LogisticRegression(model).getEclFromModel());
                break;
            case "randomForest":
                ecl.addAll(new ClassificationForest(model).getEclFromModel());
                break;
            default:
                ecl.add(new ECLElement("OUTPUT('Unable to parse stored model.');"));
                break;
        }
        ECLParser.writeToFile(ecl, outputPath);
    }

}
