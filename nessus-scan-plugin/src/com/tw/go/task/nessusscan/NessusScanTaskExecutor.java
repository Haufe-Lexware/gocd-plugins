
package com.tw.go.task.nessusscan;


import com.thoughtworks.go.plugin.api.task.*;
import com.tw.go.plugin.common.*;
import com.tw.go.plugin.common.TaskExecutor;
import org.json.JSONObject;

import java.util.Map;

public class NessusScanTaskExecutor extends TaskExecutor {

    private int oldNumHosts;
    private boolean initializingShown;
    private int oldScanProgressCurrent;
    private long lastTimeMillis;


    public NessusScanTaskExecutor(JobConsoleLogger console, Context context, Map config) {
        super(console, context, config);
    }


    public Result execute() {

        String serverToScanIp = (String) ((Map) this.config.get(NessusScanTask.SERVER_IP)).get("value");
        Log("IP(s) to scan: " + serverToScanIp);

        try {
            // get input parameter
            String policy = (String) ((Map) this.config.get(NessusScanTask.POLICY)).get("value");
            Log("Policy: " + policy);
            int nessusScanPolicy = Integer.parseInt(policy);
            String nessusScanTemplateName = (String) ((Map) this.config.get(NessusScanTask.SCANTEMPLATE)).get("value");
            Log("Scan Teamplate: " + nessusScanTemplateName);
            String issueTypeFail = (String) ((Map) this.config.get(NessusScanTask.ISSUE_TYPE_FAIL)).get("value");
            Log("Fail if: " + issueTypeFail);
            String nessusApiUrl = (String) ((Map)this.config.get(NessusScanTask.NESSUS_API_URL)).get("value");
            Log("API Url: " + nessusApiUrl);
            String nessusApiAccessKey = (String) ((Map) this.config.get(NessusScanTask.NESSUS_API_ACCESS_KEY)).get("value");
            String nessusApiSecretKey = (String) ((Map) this.config.get(NessusScanTask.NESSUS_API_SECRET_KEY)).get("value");
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

            InitProgres();

            // wait until scan is finished.
            while (!nessusClient.isScanFinished(scanId))
            {
                Thread.sleep(250);
                LogProgress(nessusClient.getScan(scanId));
            }

            // fetch the result
            JSONObject scanResult = nessusClient.getScan(scanId);
            LogSummary(scanResult);

            // export the scan
            nessusClient.exportScan(scanId, context.getWorkingDir() + "/" + exportFilename, "html");

            // delete the scan
            nessusClient.deleteScan(scanId);

            // get result issues
            return ParseResult(scanResult, issueTypeFail);

        } catch (Exception e) {
            Log("Error during execution of nessus scan. " + e.getMessage());
            return new Result(false, "Failed to execute scan for " + serverToScanIp, e);
        }

    }

    private void InitProgres(){
        oldNumHosts = 0;
        initializingShown = false;
        oldScanProgressCurrent = 0;
        lastTimeMillis = System.currentTimeMillis();
    }

    private void LogSummary(JSONObject scanResult){
        NessusScanParser resultParser = new NessusScanParser(scanResult);
        Log("-------- scan summary -------");
        Log("number of hosts scanned: " + resultParser.numHosts());
        Log("critical issues: " + resultParser.numIssuesCritical());
        Log("high issues: " + resultParser.numIssuesHigh());
        Log("medium issues: " + resultParser.numIssuesMedium());
        Log("low issues: " + resultParser.numIssuesLow());
        Log("-----------------------------");
    }

    private void LogProgress(JSONObject scanProgress) {
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
                lastTimeMillis = System.currentTimeMillis();
            }
            else
            {
                long currentTimeMillis = System.currentTimeMillis();
                if((lastTimeMillis + 1000 * 60) < currentTimeMillis) {
                    lastTimeMillis = currentTimeMillis;
                    Log("Done ...");
                }
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

    private Result ParseResult(JSONObject scanResult, String issueTypeFail) {

        NessusScanParser resultParser = new NessusScanParser(scanResult);
        if("critical".equals(issueTypeFail))
        {
            if (resultParser.numIssuesCritical() > 0)
            {
                return new Result(false, "Failed: at least 1 critical issue");
            }
            return new Result(true, "No critical issue found");
        }
        if("high".equals(issueTypeFail))
        {
            if ((resultParser.numIssuesCritical() + resultParser.numIssuesHigh()) > 0 )
            {
                return new Result(false, "Failed: at least 1 critical or high issue");
            }
            return new Result(true, "No critical or high issue found");
        }

        if("medium".equals(issueTypeFail))
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

    protected String getPluginLogPrefix(){
        return "[Nessus Scan Task Plugin] ";
    }

 }