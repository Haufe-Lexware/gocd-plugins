package com.tw.go.task.sonarqualitygate.enumeration;

/**
 * Enum for get url of SonarQube
 * 
 * @author fochoac
 *
 */
public enum EndpointSonarEnum {
    // sonarqube version
	URI_SONAR_VERSION("/server/version"),
	// Since Sonarqube version 5.3 
	URI_SONAR_QUALITYGATES_PROJECT_STATUS("/qualitygates/project_status?projectKey=%1$s"),
	// Until Sonarqube version 6.2
	URI_SONAR_ANALYSIS_DATE_UNTIL_62("/events?resource=%1$s"),
	// Since Sonarqube 6.3
	URI_SONAR_ANALYSIS_DATE_SINCE_63("/project_analyses/search?project=%1$s&category=VERSION");
	private static final double SONAR_VERSION_62 = 6.2d;

	private String uri;

	private EndpointSonarEnum(String uri) {
		this.uri = uri;
	}

	/**
	 * Method to obtain the url that contains the last date of analysis of the
	 * project.
	 * 
	 * @param sonarVersion Sonar version
	 * @return Uri
	 */
	public static final String getUriAnalisysDate(final String sonarVersion) {
		if (sonarVersion == null || sonarVersion.trim().isEmpty()) {
			return null;
		}
		if (isSonarVersionUntil62(sonarVersion)) {
			return URI_SONAR_ANALYSIS_DATE_UNTIL_62.getUri();
		}

		return URI_SONAR_ANALYSIS_DATE_SINCE_63.getUri();

	}

	public static final boolean isSonarVersionUntil62(final String sonarVersion) {
		String[] arrayVersion = sonarVersion.split("\\.");
		double version = Double.parseDouble(arrayVersion[0] + "." + arrayVersion[1]);
		return version <= SONAR_VERSION_62;
	}

	public String getUri() {
		return uri;
	}

}
