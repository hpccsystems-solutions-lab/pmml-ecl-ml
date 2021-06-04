package com.hpccsystems.pmml2ecl.ecl;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

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

        String[] cmd1 = { "cd", "./obj/ECLDir"};
        String[] cmd2 = { "sh", "this_exec.sh"};
        Runtime.getRuntime().exec(cmd1);
        Runtime.getRuntime().exec(cmd2);
    }
}
