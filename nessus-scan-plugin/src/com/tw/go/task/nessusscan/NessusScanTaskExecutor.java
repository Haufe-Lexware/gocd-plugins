
package com.tw.go.task.nessusscan;

import com.thoughtworks.go.plugin.api.task.*;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class NessusScanTaskExecutor {

    private JobConsoleLogger console;

    public NessusScanTaskExecutor(JobConsoleLogger console)
    {
        this.console = console;
    }

    public Result execute( Map config, Context context) {

        String serverToScanIp = (String) ((Map) config.get(NessusScanTask.SERVER_IP)).get("value");
        Log("IP(s) to scan: " + serverToScanIp);

        try {
            // get input parameter
            String policy = (String) ((Map) config.get(NessusScanTask.POLICY)).get("value");
            Log("Policy: " + policy);
            int nessusScanPolicy = Integer.parseInt(policy);
            String nessusScanTemplateName = (String) ((Map) config.get(NessusScanTask.SCANTEMPLATE)).get("value");
            Log("Scan Teamplate: " + nessusScanTemplateName);
            String issueTypeFail = (String) ((Map) config.get(NessusScanTask.ISSUE_TYPE_FAIL)).get("value");
            Log("Fail if: " + issueTypeFail);
            String nessusApiUrl = (String) ((Map) config.get(NessusScanTask.NESSUS_API_URL)).get("value");
            Log("API Url: " + nessusApiUrl);
            String nessusApiAccessKey = (String) ((Map) config.get(NessusScanTask.NESSUS_API_ACCESS_KEY)).get("value");
            String nessusApiSecretKey = (String) ((Map) config.get(NessusScanTask.NESSUS_API_SECRET_KEY)).get("value");
            String exportFilename = "nessusScanResult.html";

            // create a scan client
            NessusClient nessusClient = new NessusClient(nessusApiUrl, nessusApiAccessKey, nessusApiSecretKey);
            Log("nessus client created");

            // get scan template uuid
            String scanTemlateUuid = nessusClient.getScanTemplateUuid(nessusScanTemplateName);
            Log("scan template uuid: " + scanTemlateUuid);

            // create scan
            int scanId = nessusClient.createScan("TestScan", "Test Scan Description", serverToScanIp, scanTemlateUuid, nessusScanPolicy );
            Log("nessus scan id = " + String.valueOf(scanId));

            // run the scan
            String scanUuid = nessusClient.launchScan(scanId);

            // wait until scan is finished.
            while (!nessusClient.isScanFinished(scanId))
            {
                Thread.sleep(250);
                JSONObject scanProgress = nessusClient.getScan(scanId);
                NessusScanParser progressParser = new NessusScanParser(scanProgress);
                Log(progressParser.scanProgressCurrent() + " of " + progressParser.scanProgressTotal() + " finished");
            }

            // fetch the result
            JSONObject scanResult = nessusClient.getScan(scanId);

            NessusScanParser resultParser = new NessusScanParser(scanResult);
            Log("-------- scan summary -------");
            Log("number of hosts scanned: " + String.valueOf(resultParser.numHosts()));
            Log("critical issues: " + String.valueOf(resultParser.numIssuesCritical()));
            Log("high issues: " + String.valueOf(resultParser.numIssuesHigh()));
            Log("medium issues: " + String.valueOf(resultParser.numIssuesMedium()));
            Log("low issues: " + String.valueOf(resultParser.numIssuesLow()));
            Log("-----------------------------");

            // export the scan
            nessusClient.exportScan(scanId, context.getWorkingDir() + "/" + exportFilename, "html");

            // delete the scan
            nessusClient.deleteScan(scanId);

            // get result issues
            return parseResult(scanResult, issueTypeFail);

        } catch (Exception e) {
            Log("Error during execution of nessus scan. " + e.getMessage());
            return new Result(false, "Failed to execute scan for " + serverToScanIp, e);
        }

    }

    private Result parseResult(JSONObject scanResult, String issueTypeFail) {
        return new Result(false, "Failed, we have to parse the result");
    }

    private void Log(String message)
    {
        this.console.printLine("[Nessus Scan Task Plugin] " + message);
    }


    private Result runCommand(Context taskContext, JSONObject config, JobConsoleLogger console) throws Exception {

        throw new Exception(config.toString());
       /*
        ProcessBuilder curl = createCurlCommandWithOptions(taskContext, taskConfig);
        console.printLine("Launching command: " + curl.command());
        curl.environment().putAll(taskContext.getEnvironmentVariables());
        console.printEnvironment(curl.environment());

        Process curlProcess = curl.start();
        console.readErrorOf(curlProcess.getErrorStream());
        console.readOutputOf(curlProcess.getInputStream());

        int exitCode = curlProcess.waitFor();
        curlProcess.destroy();

        if (exitCode != 0) {
            return new Result(false, "Failed downloading file. Please check the output");
        }
        */
        //return new Result(true, "Scan result file: " );
    }

    /*
    ProcessBuilder createCurlCommandWithOptions(Context taskContext, Config taskConfig) {
        String destinationFilePath = taskContext.getWorkingDir() + "/" + CURLED_FILE;

        List<String> command = new ArrayList<String>();
        command.add("nessusscan");
        command.add(taskConfig.getRequestType());
        if (taskConfig.getSecureConnection().equals("no")) {
            command.add("--insecure");
        }
        if (taskConfig.getAdditionalOptions() != null && !taskConfig.getAdditionalOptions().trim().isEmpty()) {
            String parts[] = taskConfig.getAdditionalOptions().split("\\s+");
            for (String part : parts) {
                command.add(part);
            }
        }
        command.add("-o");
        command.add(destinationFilePath);
        command.add(taskConfig.getUrl());

        return new ProcessBuilder(command);
    }
    */
}