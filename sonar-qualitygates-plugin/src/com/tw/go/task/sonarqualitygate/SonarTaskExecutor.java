
package com.tw.go.task.sonarqualitygate;

import java.security.GeneralSecurityException;
import java.util.Map;

import org.json.JSONObject;

import com.thoughtworks.go.plugin.api.task.JobConsoleLogger;
import com.tw.go.plugin.common.Context;
import com.tw.go.plugin.common.GoApiClient;
import com.tw.go.plugin.common.GoApiConstants;
import com.tw.go.plugin.common.Result;
import com.tw.go.plugin.common.TaskExecutor;
import com.tw.go.task.sonarqualitygate.config.SonarConfig;

@SuppressWarnings("rawtypes")
public class SonarTaskExecutor extends TaskExecutor {

	private String sonarVersion;
	private SonarConfig sonarConfig;

	public SonarTaskExecutor(JobConsoleLogger console, Context context, Map config) {
		super(console, context, config);
		this.sonarConfig = new SonarConfig(config);
	}

	public Result execute() throws Exception {

		String sonarProjectKey = sonarConfig.getSonarProjectKey();
		log("checking quality gate result for: " + sonarProjectKey);

		try {
			// get input parameter
			String stageName = sonarConfig.getStageName();
			String jobName = sonarConfig.getJobName();
			String jobCounter = sonarConfig.getJobCounter();

			String sonarApiUrl = sonarConfig.getSonarApiUrl();
			log("API Url: " + sonarApiUrl);
			String issueTypeFail = sonarConfig.getIssueTypeFail();
			log("Fail if: " + issueTypeFail);

			SonarClient sonarClient = new SonarClient(sonarApiUrl);
			this.sonarVersion = sonarClient.getSonarVersion();
			log("SonarQube Version: " + sonarVersion);
//            // This might need some auth!
//            Map envVars = context.getEnvironmentVariables();
//            if (envVars.get(GoApiConstants.ENVVAR_NAME_SONAR_USER) != null &&
//                    envVars.get(GoApiConstants.ENVVAR_NAME_SONAR_USER_PASSWORD) != null) {
//
//                sonarClient.setBasicAuthentication(envVars.get(GoApiConstants.ENVVAR_NAME_SONAR_USER).toString(), envVars.get(GoApiConstants.ENVVAR_NAME_SONAR_USER_PASSWORD).toString());
//                log("Logged in as '" + envVars.get(GoApiConstants.ENVVAR_NAME_SONAR_USER).toString() + "' to get the project's quality gate");
//            } else {
//                log(" Requesting project's quality gate anonymously."); 
//            }

			// get quality gate details
			JSONObject projectStatus = sonarClient.getProjectWithQualityGateDetails(sonarProjectKey);
			String lastAnalisysDate = sonarClient.getLastAnalisysDate(sonarProjectKey, this.sonarVersion);
			String lastDate = SonarParser.getInstance(lastAnalisysDate, sonarVersion).getLastAnalysisDate();
			String lastVersion = SonarParser.getInstance(lastAnalisysDate, sonarVersion).getProjectVersion();

			if (!("".equals(stageName)) && !("".equals(jobName)) && !("".equals(jobCounter))) {
				String scheduledTime = getScheduledTime();

				String resultDate = lastDate;
				resultDate = new StringBuilder(resultDate).insert(resultDate.length() - 2, ":").toString();

				int timeout = 0;
				int timeoutTime = 60000;
				int timeLimit = 300000;
				log("scheduledTime: " + scheduledTime);
				log("resultDate: " + resultDate);
				while (compareDates(resultDate, scheduledTime) <= 0) {
					log("scheduledTime while: " + scheduledTime);
					log("resultDate while: " + resultDate);
					log("Scan result is older than the start of the pipeline. Waiting for a newer scan ...");

					projectStatus = sonarClient.getProjectWithQualityGateDetails(sonarProjectKey);
					lastAnalisysDate = sonarClient.getLastAnalisysDate(sonarProjectKey, this.sonarVersion);
					lastDate = SonarParser.getInstance(lastAnalisysDate, sonarVersion).getLastAnalysisDate();
					lastVersion = SonarParser.getInstance(lastAnalisysDate, sonarVersion).getProjectVersion();
					timeout = timeout + timeoutTime;

					resultDate = lastDate;
					resultDate = new StringBuilder(resultDate).insert(resultDate.length() - 2, ":").toString();

					if (timeout > timeLimit) {

						log("No new scan has been found !");

						log("Date of Sonar scan: " + lastDate);
						log("Version of Sonar scan: " + lastVersion);

						return new Result(false, "Failed to get a newer quality gate for " + sonarProjectKey
								+ ". The present quality gate is older than the start of the Sonar scan task.");
					}

					Thread.sleep(timeoutTime);

				}

				log("Date of Sonar scan: " + lastDate);
				log("Version of Sonar scan: " + lastVersion);

				// check that a quality gate is returned
				String qgResult = SonarParser.getInstance(projectStatus).getProjectQualityGateStatus();

				// get result issues
				return parseResult(qgResult, issueTypeFail);

			} else {

				log("Date of Sonar scan: " + lastDate);
				log("Version of Sonar scan: " + lastVersion);

				// check that a quality gate is returned
				String qgResult = SonarParser.getInstance(projectStatus).getProjectQualityGateStatus();

				// get result issues
				return parseResult(qgResult, issueTypeFail);
			}

		} catch (Exception e) {
			log("Error during get or parse of quality gate result. Please check if a quality gate is defined\n"
					+ e.getMessage());
			return new Result(false, "Failed to get quality gate for " + sonarProjectKey
					+ ". Please check if a quality gate is defined\n", e);
		}
	}

	private Result parseResult(String qgResult, String issueTypeFail) {

		switch (issueTypeFail) {
		case "error":
			if ("ERROR".equals(qgResult)) {
				return new Result(false, "At least one Error in Quality Gate");
			}
			break;
		case "warning":
			if ("ERROR".equals(qgResult) || "WARN".equals(qgResult)) {
				return new Result(false, "At least one Error or Warning in Quality Gate");
			}
			break;
		}
		return new Result(true, "SonarQube quality gate passed");
	}

	protected String getScheduledTime() throws GeneralSecurityException {
		Map envVars = context.getEnvironmentVariables();
		GoApiClient client = new GoApiClient(envVars.get(GoApiConstants.ENVVAR_NAME_GO_SERVER_URL).toString());
		try {
			// get go build user authorization
			if (envVars.get(GoApiConstants.ENVVAR_NAME_GO_BUILD_USER) != null
					&& envVars.get(GoApiConstants.ENVVAR_NAME_GO_BUILD_USER_PASSWORD) != null) {

				client.setBasicAuthentication(envVars.get(GoApiConstants.ENVVAR_NAME_GO_BUILD_USER).toString(),
						envVars.get(GoApiConstants.ENVVAR_NAME_GO_BUILD_USER_PASSWORD).toString());

				log("Logged in as '" + envVars.get(GoApiConstants.ENVVAR_NAME_GO_BUILD_USER).toString() + "'");
			} else {
				log("No login set. Going anonymous.");
			}

			String scheduledTime = client.getJobProperty(envVars.get("GO_PIPELINE_NAME").toString(),
					envVars.get("GO_PIPELINE_COUNTER").toString(), sonarConfig.getStageName(),
					sonarConfig.getJobCounter(), sonarConfig.getJobName(), "cruise_timestamp_01_scheduled");

			return scheduledTime;

		} catch (Exception e) {
			log(e.toString());
			return null;
		}
	}

	protected int compareDates(String date1, String date2) {
		return (date1.compareTo(date2));
	}

	protected String getPluginLogPrefix() {
		return "[SonarQube Quality Gate Plugin] ";
	}
}