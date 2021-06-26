package test;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.util.Scanner;

public class CommonTestOperations {

    public static String getFileContents(String absoluteFilePath) throws Exception {
        File file = new File(absoluteFilePath);
        Scanner in = new Scanner(file);
        String fileContents = "";
        while(in.hasNextLine()) {
            fileContents += in.nextLine() + "\n";
        }
        in.close();
        return fileContents;
    }

    public static void writeToFile(String absoluteFilePath, String contents) throws Exception {
        File outputDir = new File(System.getProperty("user.dir") + "/output");
        outputDir.mkdirs();
        File file = new File(absoluteFilePath);
        //TODO: check for folder
        if (!file.exists()) {
            file.createNewFile();
        }
        FileWriter fw = new FileWriter(file.getAbsoluteFile());
        BufferedWriter bw = new BufferedWriter(fw);
        bw.write(contents);
        bw.close();
    }

}
