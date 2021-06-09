package com.hpccsystems.pmml2ecl.ecl;

import org.hpccsystems.ws.client.HPCCWsClient;
import org.hpccsystems.ws.client.HPCCWsWorkUnitsClient;
import org.hpccsystems.ws.client.gen.axis2.wsworkunits.v1_81.ECLSourceFile;
import org.hpccsystems.ws.client.gen.axis2.wsworkunits.v1_81.WURunResponse;
import org.hpccsystems.ws.client.platform.Platform;
import org.hpccsystems.ws.client.platform.Workunit;
import org.hpccsystems.ws.client.wrappers.wsworkunits.WorkunitWrapper;

import java.io.File;
import java.util.Scanner;
import java.util.regex.Pattern;

public class ECLCompiler {
    
    static String currDir = System.getProperty("user.dir");

    public ECLCompiler(String eclFilePath) throws Exception {
        Platform platform = Platform.get("http", "play.hpccsystems.com", 8010, "aparra", "");
        HPCCWsWorkUnitsClient connector = platform.getWsClient().getWsWorkunitsClient();
        WorkunitWrapper wu = new WorkunitWrapper();
        wu.setECL(getECL(eclFilePath));
        wu.setCluster("thor");
        WURunResponse results = connector.createAndRunWUFromECL(wu);
        System.out.println(results.getResults());
    }

    private String getECL(String filepath) throws Exception {
        Pattern acceptableEndings = Pattern.compile("(\\.(ecl))$", Pattern.CASE_INSENSITIVE);
        if (!acceptableEndings.matcher(filepath).find()) {
            throw new Exception("File type not accepted.");
        }
        File file = new File(filepath);
        Scanner in = new Scanner(file);
        String fileContents = "";
        while(in.hasNextLine()) {
            fileContents += in.nextLine() + "\n";
        }
        in.close();
        return fileContents;
    }
}
