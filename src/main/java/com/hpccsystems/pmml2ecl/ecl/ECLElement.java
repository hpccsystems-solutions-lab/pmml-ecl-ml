package com.hpccsystems.pmml2ecl.ecl;

public class ECLElement {

    private String line;
    
    public ECLElement(String line) {
        this.line = line;
    }

    @Override
    public String toString() {
        return line;
    }


}
