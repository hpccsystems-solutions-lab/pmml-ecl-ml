package com.hpccsystems.pmml2ecl.pmml;

import java.util.LinkedList;

import com.hpccsystems.pmml2ecl.ecl.ECLElement;

public class PMMLConverter {
    
//find first with functionName/algorithmName
//get functionName and tag
//
    LinkedList<ECLElement> ecl;
    public PMMLConverter(PMMLElement root) {
        ecl = new LinkedList<>();
        ecl.add(new ECLElement("IMPORT ML_Core;"));
    }

    public String eclToString() {
        String full = "";
        for (int i = 0; i < ecl.size(); i++) {
            full += ecl.get(i).toString() + ";\n";
        }
        return full;
    }

}
