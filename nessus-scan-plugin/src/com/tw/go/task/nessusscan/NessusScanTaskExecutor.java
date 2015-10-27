
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
            nessusClient.launchScan(scanId);

            int oldNumHosts = 0;
            boolean initializingShown = false;
            int oldScanProgressCurrent = 0;
            // wait until scan is finished.
            while (!nessusClient.isScanFinished(scanId))
            {
                Thread.sleep(250);
                JSONObject scanProgress = nessusClient.getScan(scanId);
                NessusScanParser progressParser = new NessusScanParser(scanProgress);
                int NumHosts = progressParser.numHosts();
                if(NumHosts > 0)
                {
                    if (oldNumHosts != NumHosts){
                        oldNumHosts = NumHosts;
                        Log("Number of Hosts to scan: " + NumHosts);
                    }

                    int scanProgressCurrent = progressParser.scanProgressCurrent();
                    if(scanProgressCurrent != oldScanProgressCurrent)
                    {
                        oldScanProgressCurrent = scanProgressCurrent;
                        int scanPercent = (100 * scanProgressCurrent) / progressParser.scanProgressTotal();
                        Log("Done " + scanPercent + "%");
                    }
                }
                else
                {
                    if(!initializingShown)
                    {
                        initializingShown = true;
                        Log("Initializing scan...");
                    }
                }

            }

            // fetch the result
            JSONObject scanResult = nessusClient.getScan(scanId);

            NessusScanParser resultParser = new NessusScanParser(scanResult);
            Log("-------- scan summary -------");
            Log("number of hosts scanned: " + resultParser.numHosts());
            Log("critical issues: " + resultParser.numIssuesCritical());
            Log("high issues: " + resultParser.numIssuesHigh());
            Log("medium issues: " + resultParser.numIssuesMedium());
            Log("low issues: " + resultParser.numIssuesLow());
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

        NessusScanParser resultParser = new NessusScanParser(scanResult);
        if(issueTypeFail.equals("critical"))
        {
            if (resultParser.numIssuesCritical() > 0)
            {
                return new Result(false, "Failed: at least 1 critical issue");
            }
            return new Result(true, "No critical issue found");
        }
        if(issueTypeFail.equals("high"))
        {
            if ((resultParser.numIssuesCritical() + resultParser.numIssuesHigh()) > 0 )
            {
                return new Result(false, "Failed: at least 1 critical or high issue");
            }
            return new Result(true, "No critical or high issue found");
        }

        if(issueTypeFail.equals("medium"))
        {
            if ((resultParser.numIssuesCritical() + resultParser.numIssuesHigh() + resultParser.numIssuesMedium()) > 0 )
            {
                return new Result(false, "Failed: at least 1 critical, high or medium issue");
            }
            return new Result(true, "No critical, high or medium issue found");
        }

        if ((resultParser.numIssuesCritical() + resultParser.numIssuesHigh() + resultParser.numIssuesMedium() + resultParser.numIssuesLow()) > 0 )
        {
            return new Result(false, "Failed: at least 1 issue found ");
        }
        return new Result(true, "No issue found");

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