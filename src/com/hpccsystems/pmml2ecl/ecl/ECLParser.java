package com.hpccsystems.pmml2ecl.ecl;

import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;

public class ECLParser {

    private LinkedList<ECLElement> allElems;
    
    public ECLParser(String fileContents, boolean diff) {
        allElems = new LinkedList<>();
        String cont = fileContents;
        int index = 0;
        while (index >= 0 && cont.length() > 0) {
            //TODO: Comments bruh.
            index = cont.indexOf(';', 0);
            allElems.add(new ECLElement(cont.substring(0, index).replaceAll("(//).+\n", "").trim()));
            cont = cont.substring(index + 1);
        }
    }

    public LinkedList<ECLElement> getElems() {
        return allElems;
    }

}
