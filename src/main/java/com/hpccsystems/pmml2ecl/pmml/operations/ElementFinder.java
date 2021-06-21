package com.hpccsystems.pmml2ecl.pmml.operations;

import com.hpccsystems.pmml2ecl.pmml.PMMLElement;

import java.util.ArrayList;
import java.util.List;

public class ElementFinder {

    public static boolean hasElementWithTagContent(List<PMMLElement> rows, String tag, String content) {
        for (PMMLElement row : rows) {
            PMMLElement findElem = row.firstNodeWithTag(tag);
            if (findElem != null && findElem.content.equals(content)) {
                return true;
            }
        }
        return false;
    }

    public static List<PMMLElement> getAllWhereHasTagContent(List<PMMLElement> rows, String tag, String content) {
        List<PMMLElement> returnElements = new ArrayList<>();

        for (PMMLElement row : rows) {
            PMMLElement findElem = row.firstNodeWithTag(tag);
            if (findElem != null && findElem.content.equals(content)) {
                returnElements.add(row);
            }
        }

        return returnElements;
    }

    public static List<PMMLElement> getAllWhereHasTagInRange(List<PMMLElement> rows, String tag, int from, int to) {
        List<PMMLElement> returnElements = new ArrayList<>();

        for (PMMLElement row : rows) {
            PMMLElement findElem = row.firstNodeWithTag(tag);
            if (findElem != null) {
                try {
                    Integer value = Integer.parseInt(findElem.content);
                    if (from <= value && value < to) {
                        returnElements.add(row);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        return returnElements;
    }

}
