package com.hpccsystems.pmml2ecl;

import java.util.regex.Pattern;

public class Converter {

    public static void main(String[] args) throws Exception {
        if (args.length < 1) {
            throw new Exception("Parameters not defined.");
        }
        Pattern acceptablePMMLEndings = Pattern.compile("(\\.(xml|pmml))$", Pattern.CASE_INSENSITIVE);
        Pattern acceptableECLEndings = Pattern.compile("(\\.(ecl))$", Pattern.CASE_INSENSITIVE);
        if (acceptablePMMLEndings.matcher(args[0]).find()) {
            if (args.length == 1) {
                new PMMLConverter(args[0]);
            } else if (args.length == 2 && acceptableECLEndings.matcher(args[1]).find()) {
                new PMMLConverter(args[0], args[1]);
            } else {
                throw new Exception("Too many files were given or file extensions not accepted.");
            }
        } else if (acceptableECLEndings.matcher(args[0]).find()) {
            if (args.length == 1) {
                new ECLConverter(args[0]);
            } else if (args.length == 2 && acceptablePMMLEndings.matcher(args[1]).find()) {
                new ECLConverter(args[0], args[1]);
            } else {
                throw new Exception("Too many files were given or file extensions not accepted.");
            }
        }
    }

}
