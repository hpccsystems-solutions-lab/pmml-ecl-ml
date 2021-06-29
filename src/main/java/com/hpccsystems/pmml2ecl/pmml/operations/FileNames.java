package com.hpccsystems.pmml2ecl.pmml.operations;

public class FileNames {

    public static String insertNumberToFilePath(String absoluteFilePath, int number) {
        int ext = absoluteFilePath.lastIndexOf(".");
        return absoluteFilePath.substring(0, ext) + number + absoluteFilePath.substring(ext);
    }

    public static String getDirectory(String absoluteFilePath) {
        return absoluteFilePath.substring(0, absoluteFilePath.lastIndexOf("/"));
    }

}
