package com.hpccsystems.pmml2ecl.ecl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.hpccsystems.pmml2ecl.Node;
import com.hpccsystems.pmml2ecl.pmml.PMMLElement;

public class XMLMLConverter {

    private PMMLElement rootData;

    /**
     * Just a note, this is XML not PMML. It just so hsppens to work for XML in general lol.
     */
    public XMLMLConverter(PMMLElement rootData) {
        this.rootData = rootData;
    }
    
    public PMMLElement toLinearRegression() {
        PMMLElement linearRegressionRoot = new PMMLElement("PMML", "version=\"4.0\"", "", false);

        PMMLElement header = new PMMLElement("Header", "", "", true);

        linearRegressionRoot.addChild(header);

        PMMLElement model = rootData.firstNodeWithTag("Dataset");

        List<Node> fields = new ArrayList<>();
        List<Node> coefficients = new ArrayList<>();

        for(Node elem : model.childNodes) {
            String field = ((PMMLElement) elem).firstNodeWithTag("number").content;
            String value = ((PMMLElement) elem).firstNodeWithTag("value").content;
            Map<String, String> attribs = new HashMap<>();
            attribs.put("name", field);
            fields.add(new PMMLElement("MiningField", attribs, null, true));
            Map<String, String> attribs2 = new HashMap<>();
            attribs2.put("name", field);
            attribs2.put("coefficient", value);
            coefficients.add(new PMMLElement("NumericPredictor", attribs2, null, true));
        }

        PMMLElement schema = new PMMLElement("MiningSchema", new HashMap<>(), fields, false);
        PMMLElement table = new PMMLElement("RegressionTable", new HashMap<>(), coefficients, false);

        Map<String, String> regMap = new HashMap<>();
        regMap.put("functionName", "regression");
        regMap.put("algorithmName", "LinearRegression");
        PMMLElement regModel = new PMMLElement("RegressionModel", regMap, new ArrayList<>(), false);

        regModel.addChild(schema);
        regModel.addChild(table);

        linearRegressionRoot.addChild(regModel);

        return linearRegressionRoot;
    }

}
