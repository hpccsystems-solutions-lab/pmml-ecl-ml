package com.hpccsystems.pmml2ecl.ecl;

import java.util.LinkedList;
import java.util.List;

public class ECLParser {
    
    public ECLParser(String fileContents, boolean diff) {
        List<ECLElement> elems = new LinkedList<>();
        String cont = fileContents;
        int index = 0;
        while (index >= 0 && cont.length() > 0) {
            //TODO: Comments bruh.
            index = fileContents.indexOf(';', 0);
            elems.add(new ECLElement(cont.substring(0, index)));
            cont = cont.substring(index + 1);
        }
    }

}
