package com.tw.go.task.sonarqualitygate.config;

import java.util.Map;

import com.tw.go.plugin.common.GoApiConstants;

/**
 * class model for sonarqube
 * 
 * @author fochoa
 *
 */
@SuppressWarnings("rawtypes")
public class SonarConfig {
	// definition of constants
	public static final String ISSUE_TYPE_FAIL = "IssueTypeFail";
	public static final String SONAR_API_URL = "SonarApiUrl";
	public static final String SONAR_PROJECT_KEY = "SonarProjectKey";
	public static final String STAGE_NAME = "StageName";
	public static final String JOB_NAME = "JobName";
	public static final String JOB_COUNTER = "JobCounter";
	// definition of parameters
	private String issueTypeFail;
	private String sonarApiUrl;
	private String sonarProjectKey;
	private String stageName;
	private String jobName;
	private String jobCounter;

	public SonarConfig(Map config) { 
		issueTypeFail = getValue(config, ISSUE_TYPE_FAIL);
		sonarApiUrl = getValue(config, SONAR_API_URL);
		sonarProjectKey = getValue(config, SONAR_PROJECT_KEY);
		stageName = getValue(config, STAGE_NAME);
		jobName = getValue(config, JOB_NAME);
		jobCounter = getValue(config, JOB_COUNTER);
	}

	private String getValue(Map config, String property) {
		return (String) ((Map) config.get(property)).get(GoApiConstants.PROPERTY_NAME_VALUE);
	}

	public String getIssueTypeFail() {
		return issueTypeFail;
	}

	public void setIssueTypeFail(String issueTypeFail) {
		this.issueTypeFail = issueTypeFail;
	}

	public String getSonarApiUrl() {
		return sonarApiUrl;
	}

	public void setSonarApiUrl(String sonarApiUrl) {
		this.sonarApiUrl = sonarApiUrl;
	}

	public String getSonarProjectKey() {
		return sonarProjectKey;
	}

	public void setSonarProjectKey(String sonarProjectKey) {
		this.sonarProjectKey = sonarProjectKey;
	}

	public String getStageName() {
		return stageName;
	}

	public void setStageName(String stageName) {
		this.stageName = stageName;
	}

	public String getJobName() {
		return jobName;
	}

	public void setJobName(String jobName) {
		this.jobName = jobName;
	}

	public String getJobCounter() {
		return jobCounter;
	}

	public void setJobCounter(String jobCounter) {
		this.jobCounter = jobCounter;
	}
}
