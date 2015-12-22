
package com.tw.go.task.sonarqualitygate;


import com.thoughtworks.go.plugin.api.task.*;
import com.tw.go.plugin.common.*;
import com.tw.go.plugin.common.TaskExecutor;
import org.json.JSONObject;

import java.util.Map;

public class SonarTaskExecutor extends TaskExecutor {

    private int oldNumHosts;
    private boolean initializingShown;
    private int oldScanProgressCurrent;
    private long lastTimeMillis;


    public SonarTaskExecutor(JobConsoleLogger console, Context context, Map config) {
        super(console, context, config);
    }


    public Result execute() {

        String sonarProjectKey = (String) ((Map) this.config.get(SonarScanTask.SONAR_PROJECT_KEY)).get(GoApiConstants.PROPERTY_NAME_VALUE);
        log("checking quality gate result for: " + sonarProjectKey);

        try {
            // get input parameter
            String sonarApiUrl = (String) ((Map)this.config.get(SonarScanTask.SONAR_API_URL)).get(GoApiConstants.PROPERTY_NAME_VALUE);
            log("API Url: " + sonarApiUrl);
            String issueTypeFail = (String) ((Map) this.config.get(SonarScanTask.ISSUE_TYPE_FAIL)).get(GoApiConstants.PROPERTY_NAME_VALUE);
            log("Fail if: " + issueTypeFail);

            SonarClient sonarClient = new SonarClient(sonarApiUrl);

            // get quality gate details
            JSONObject result = sonarClient.getProjectWithQualityGateDetails(sonarProjectKey);

            SonarParser parser = new SonarParser(result);

            // check that a quality gate is returned
            JSONObject qgDetails = parser.GetQualityGateDetails();

            String qgResult = qgDetails.getString("level");

            // get result issues
            return parseResult(qgResult, issueTypeFail);

        } catch (Exception e) {
            log("Error during get or parse of quality gate result. Please check if a quality gate is defined" + e.getMessage());
            return new Result(false, "Failed to get quality gate for " + sonarProjectKey + ". Please check if a quality gate is defined", e);
        }
    }

    private Result parseResult(String qgResult, String issueTypeFail) {

        switch (issueTypeFail) {
            case "error" :
                if("ERROR".equals(qgResult))
                {
                    return new Result(false, "At least one Error in Quality Gate");
                }
                break;
            case "warning" :
                if("ERROR".equals(qgResult) || "WARN".equals(qgResult))
                {
                    return new Result(false, "At least one Error or Warning in Quality Gate");
                }
                break;
        }
        return new Result(true, "SonarQube quality gate passed");
    }

    protected String getPluginLogPrefix(){
        return "[SonarQube Quality Gate Plugin] ";
    }

 }