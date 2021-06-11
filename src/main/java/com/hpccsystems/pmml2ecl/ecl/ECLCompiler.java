package com.hpccsystems.pmml2ecl.ecl;

import org.hpccsystems.ws.client.HPCCWsWorkUnitsClient;
import org.hpccsystems.ws.client.gen.axis2.wsworkunits.v1_81.WURunResponse;
import org.hpccsystems.ws.client.platform.Platform;
import org.hpccsystems.ws.client.wrappers.wsworkunits.ECLQueryWrapper;
import org.hpccsystems.ws.client.wrappers.wsworkunits.WorkunitWrapper;

import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.regex.Pattern;
import org.json.*;

public class ECLCompiler {

    private static String currDir = System.getProperty("user.dir");

    /**
     * Takes in a filePath, sends the .ecl to a ECL Watch server for compiling and runnning.
     * Result is returned and stored as Result.xml in obj/
     * @param eclFilePath absolute eclFilePath to send and run on Thor
     * @throws Exception
     */
    public ECLCompiler(String eclFilePath) throws Exception {

        if (!checkECLExtension(eclFilePath)) {
            throw new Exception("Not a valid .ecl file");
        }

        JSONObject settings = new JSONObject(getJSONString());

        String eclBinary = settings.getString("eclBinary");
        if (eclBinary == null)
        {
            throw new Exception("ECL Binary not defined");
        }

        ArrayList<String> paramList = new ArrayList<>();
        paramList.add(eclBinary);
        paramList.add("run");
        paramList.add("thor");
        paramList.add(eclFilePath);

        String server = settings.getString("server");
        if (server != null) {
            paramList.add("-s=" + server);
        } else {
            throw new Exception("Server not defined");
        }

        Integer port = settings.getInt("port");
        paramList.add("--port=" + port);

        String user = settings.getString("user");
        if (user != null) {
            paramList.add("-u=" + user);
        } else {
            throw new Exception("User not defined");
        }

        String password = settings.getString("password");
        if (password != null) {
            paramList.add("-pw=" + password);
        } else {
            throw new Exception("Password not defined");
        }

        String jobName = settings.getString("jobName");
        if (jobName != null) {
            paramList.add("-n=" + jobName);
        } else {
            throw new Exception("Job Name not defined");
        }

        String[] params = new String[paramList.size()];
        paramList.toArray(params);

        ProcessBuilder pb = new ProcessBuilder(params);
        Process p = pb.start();
        try
        {
            Thread.sleep(1000);
        }
        catch (InterruptedException ex)
        {
            Thread.currentThread().interrupt();
        }
        String errorText = "";

        InputStream in = null;
        BufferedReader br = null;
        try
        {
            in = p.getErrorStream();
            br = new BufferedReader(new InputStreamReader(in));
            String lineErr;
            while ((lineErr = br.readLine()) != null)
            {
                errorText += lineErr + "\r\n";
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            throw new Exception("Error reading compile errors:"
                    + e.getMessage());
        }
        finally
        {
            try
            {
                if (br != null)
                {
                    br.close();
                }
                if (in != null)
                {
                    in.close();
                }
            }
            catch (Exception ex)
            {}
        }
        writeResultToFile(errorText, "CompileErrors.txt");
        String result = "";
        try
        {
            in = p.getInputStream();
            br = new BufferedReader(new InputStreamReader(in));
            String lineErr;
            while ((lineErr = br.readLine()) != null)
            {
                result += lineErr + "\r\n";
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            throw new Exception("Error reading compile errors:"
                    + e.getMessage());
        }
        finally
        {
            try
            {
                if (br != null)
                {
                    br.close();
                }
                if (in != null)
                {
                    in.close();
                }
            }
            catch (Exception ex)
            {}
        }
        writeResultToFile(result, "CompileResult.xml");
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

    private boolean checkECLExtension(String filepath) {
        Pattern acceptableEndings = Pattern.compile("(\\.(ecl))$", Pattern.CASE_INSENSITIVE);
        return acceptableEndings.matcher(filepath).find();
    }

    private static String getJSONString() throws Exception{
        File file = new File(currDir + "/config.json");
        Scanner in = new Scanner(file);
        String fileContents = "";
        while(in.hasNextLine()) {
            fileContents += in.nextLine() + "\n";
        }
        in.close();
        return fileContents;
    }

    private static void writeResultToFile(String result, String fileName) throws Exception {
        File file = new File(System.getProperty("user.dir") + "/obj/" + fileName);
        //TODO: check for folder
        if (!file.exists()) {
            file.createNewFile();
        }
        FileWriter fw = new FileWriter(file.getAbsoluteFile());
        BufferedWriter bw = new BufferedWriter(fw);
        bw.write(result);
        bw.close();
    }

}
