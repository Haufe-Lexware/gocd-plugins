
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

        String serverToScanIp = (String) ((Map) this.config.get(NessusScanTask.SERVER_IP)).get(GoApiConstants.PROPERTY_NAME_VALUE);
        log("IP(s) to scan: " + serverToScanIp);

        try {
            // get input parameter
            String policy = (String) ((Map) this.config.get(NessusScanTask.POLICY)).get(GoApiConstants.PROPERTY_NAME_VALUE);
            log("Policy: " + policy);
            int nessusScanPolicy = Integer.parseInt(policy);
            String nessusScanTemplateName = (String) ((Map) this.config.get(NessusScanTask.SCANTEMPLATE)).get(GoApiConstants.PROPERTY_NAME_VALUE);
            log("Scan Teamplate: " + nessusScanTemplateName);
            String issueTypeFail = (String) ((Map) this.config.get(NessusScanTask.ISSUE_TYPE_FAIL)).get(GoApiConstants.PROPERTY_NAME_VALUE);
            log("Fail if: " + issueTypeFail);
            String nessusApiUrl = (String) ((Map)this.config.get(NessusScanTask.NESSUS_API_URL)).get(GoApiConstants.PROPERTY_NAME_VALUE);
            log("API Url: " + nessusApiUrl);
            String nessusApiAccessKey = (String) ((Map) this.config.get(NessusScanTask.NESSUS_API_ACCESS_KEY)).get(GoApiConstants.PROPERTY_NAME_VALUE);
            String nessusApiSecretKey = (String) ((Map) this.config.get(NessusScanTask.NESSUS_API_SECRET_KEY)).get(GoApiConstants.PROPERTY_NAME_VALUE);
            String exportFilename = "nessusScanResult.html";

            // create a scan client
            NessusClient nessusClient = new NessusClient(nessusApiUrl, nessusApiAccessKey, nessusApiSecretKey);
            log("nessus client created");

            // get scan template uuid
            String scanTemlateUuid = nessusClient.getScanTemplateUuid(nessusScanTemplateName);
            log("scan template uuid: " + scanTemlateUuid);

            // create scan
            int scanId = nessusClient.createScan("TestScan", "Test Scan Description", serverToScanIp, scanTemlateUuid, nessusScanPolicy );
            log("nessus scan id = " + scanId);

            // run the scan
            nessusClient.launchScan(scanId);

            initProgres();

            // wait until scan is finished.
            while (!nessusClient.isScanFinished(scanId))
            {
                Thread.sleep(250);
                logProgress(nessusClient.getScan(scanId));
            }

            // fetch the result
            JSONObject scanResult = nessusClient.getScan(scanId);
            logSummary(scanResult);

            // export the scan
            nessusClient.exportScan(scanId, context.getWorkingDir() + "/" + exportFilename, "html");

            // delete the scan
            try
            {
                nessusClient.deleteScan(scanId);
            }
            catch(Exception e)
            {
                log("Warning: Can't remove the scan.");
            }

            // get result issues
            return parseResult(scanResult, issueTypeFail);

        } catch (Exception e) {
            log("Error during execution of nessus scan. " + e.getMessage());
            return new Result(false, "Failed to execute scan for " + serverToScanIp, e);
        }

    }

    private void initProgres(){
        oldNumHosts = 0;
        initializingShown = false;
        oldScanProgressCurrent = 0;
        lastTimeMillis = System.currentTimeMillis();
    }

    private void logSummary(JSONObject scanResult){
        NessusScanParser resultParser = new NessusScanParser(scanResult);
        log("-------- scan summary -------");
        log("number of hosts scanned: " + resultParser.numHosts());
        log("critical issues: " + resultParser.numIssuesCritical());
        log("high issues: " + resultParser.numIssuesHigh());
        log("medium issues: " + resultParser.numIssuesMedium());
        log("low issues: " + resultParser.numIssuesLow());
        log("-----------------------------");
    }

    private void logProgress(JSONObject scanProgress) {
        NessusScanParser progressParser = new NessusScanParser(scanProgress);
        int numHosts = progressParser.numHosts();
        if(numHosts > 0)
        {
            if (oldNumHosts != numHosts){
                oldNumHosts = numHosts;
                log("Number of hosts to scan: " + numHosts);
            }

            int scanProgressCurrent = progressParser.scanProgressCurrent();
            if(scanProgressCurrent != oldScanProgressCurrent)
            {
                oldScanProgressCurrent = scanProgressCurrent;
                int scanPercent = (100 * scanProgressCurrent) / progressParser.scanProgressTotal();
                log("Done " + scanPercent + "%");
                lastTimeMillis = System.currentTimeMillis();
            }
            else
            {
                long currentTimeMillis = System.currentTimeMillis();
                if((lastTimeMillis + 1000 * 60) < currentTimeMillis) {
                    lastTimeMillis = currentTimeMillis;
                    log("Done ...");
                }
            }
        }
        else
        {
            if(!initializingShown)
            {
                initializingShown = true;
                log("Initializing scan...");
            }
        }
    }

    private Result parseResult(JSONObject scanResult, String issueTypeFail) {

        NessusScanParser resultParser = new NessusScanParser(scanResult);
        switch (issueTypeFail) {
            case "critical" :
                if (resultParser.numIssuesCritical() > 0) {
                    return new Result(false, "Failed: at least 1 critical issue");
                }
                return new Result(true, "No critical issue found");
            case "high" :
                if ((resultParser.numIssuesCritical() + resultParser.numIssuesHigh()) > 0) {
                    return new Result(false, "Failed: at least 1 critical or high issue");
                }
                return new Result(true, "No critical or high issue found");
            case "medium" :
                if ((resultParser.numIssuesCritical() + resultParser.numIssuesHigh() + resultParser.numIssuesMedium()) > 0) {
                    return new Result(false, "Failed: at least 1 critical, high or medium issue");
                }
                return new Result(true, "No critical, high or medium issue found");
            default:
                if ((resultParser.numIssuesCritical() + resultParser.numIssuesHigh() + resultParser.numIssuesMedium() + resultParser.numIssuesLow()) > 0) {
                    return new Result(false, "Failed: at least 1 issue found ");
                }
                return new Result(true, "No issue found");
        }
    }

    protected String getPluginLogPrefix(){
        return "[Nessus Scan Task Plugin] ";
    }

 }