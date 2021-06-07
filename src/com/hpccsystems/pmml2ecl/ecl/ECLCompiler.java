package com.hpccsystems.pmml2ecl.ecl;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

public class ECLCompiler {
    
    static String currDir = System.getProperty("user.dir");

    public ECLCompiler(String eclFilePath) throws Exception {
        InputStream is = null;
        OutputStream os = null;
        try {
            is = new FileInputStream(eclFilePath);
            os = new FileOutputStream(currDir + "/obj/ECLDir/tmp_exec.ecl");

            // buffer size 1K
            byte[] buf = new byte[1024];

            int bytesRead;
            while ((bytesRead = is.read(buf)) > 0) {
                os.write(buf, 0, bytesRead);
            }
        } finally {
            is.close();
            os.close();
        }

        String[] cmd1 = { "cd", currDir + "/obj/ECLDir"};
        String[] cmd2 = { "sh", "./this_exec.sh"};
        Runtime.getRuntime().exec(cmd1);
        Process p = Runtime.getRuntime().exec(cmd2);
        p.waitFor();
        BufferedReader reader=new BufferedReader(new InputStreamReader(
            p.getInputStream())); 
        String line; 
        while((line = reader.readLine()) != null) { 
            System.out.println(line);
   }
    }
}
