package com.tw.go.task.sonarqualitygate;

import static org.junit.Assert.assertEquals;

import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
 
public class SonarParserTest {
	private static final String PROJECT_QUALITY_GATES = "{\"projectStatus\":{\"status\":\"OK\",\"conditions\":[{\"status\":\"OK\",\"metricKey\":\"new_reliability_rating\",\"comparator\":\"GT\",\"periodIndex\":1,\"errorThreshold\":\"1\",\"actualValue\":\"1\"},{\"status\":\"OK\",\"metricKey\":\"new_security_rating\",\"comparator\":\"GT\",\"periodIndex\":1,\"errorThreshold\":\"1\",\"actualValue\":\"1\"},{\"status\":\"OK\",\"metricKey\":\"new_maintainability_rating\",\"comparator\":\"GT\",\"periodIndex\":1,\"errorThreshold\":\"1\",\"actualValue\":\"1\"}],\"periods\":[{\"index\":1,\"mode\":\"previous_version\",\"date\":\"2019-05-04T15:55:32-0500\"}],\"ignoredConditions\":false}}";
	private static final String ANALYSIS_63 = "{\"paging\":{\"pageIndex\":1,\"pageSize\":100,\"total\":1},\"analyses\":[{\"key\":\"AWqFg8sAB39nWH190SmO\",\"date\":\"2019-05-05T01:02:34+0000\",\"events\":[{\"key\":\"AWqFg9uAB39nWH190Sxq\",\"category\":\"VERSION\",\"name\":\"1.0.0-SNAPSHOT\"}],\"projectVersion\":\"1.0.0-SNAPSHOT\",\"manualNewCodePeriodBaseline\":false}]}";
	private static final String ANALYSIS_62 = "[{\"id\":\"5\",\"rk\":\"org.report:report4j\",\"n\":\"Green (was Red)\",\"c\":\"Alert\",\"dt\":\"2019-05-05T00:40:29+0000\",\"ds\":\"\"},{\"id\":\"6\",\"rk\":\"org.report:report4j\",\"n\":\"1.0.0-SNAPSHOT\",\"c\":\"Version\",\"dt\":\"2019-05-05T00:40:29+0000\"},{\"id\":\"2\",\"rk\":\"org.report:report4j\",\"n\":\"Red (was Green)\",\"c\":\"Alert\",\"dt\":\"2019-05-01T03:11:26+0000\",\"ds\":\"Code Smells > 0\"}]";

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void testGetProjectQualityGateStatus() {
		String result = SonarParser.getInstance(new JSONObject(PROJECT_QUALITY_GATES)).getProjectQualityGateStatus();
		assertEquals("OK", result);
	}

	@Test
	public void testGetLastAnalysisDate() {
		String date63 = SonarParser.getInstance(ANALYSIS_63, "7.0").getLastAnalysisDate();
		assertEquals("2019-05-05T01:02:34+0000", date63);
		String date62 = SonarParser.getInstance(ANALYSIS_62, "5.6.3").getLastAnalysisDate();
		assertEquals("2019-05-05T00:40:29+0000", date62);
	}

	@Test
	public void testGetProjectVersion() {
		String ver63 = SonarParser.getInstance(ANALYSIS_63, "7.0").getProjectVersion();
		assertEquals("1.0.0-SNAPSHOT", ver63);
		String ver62 = SonarParser.getInstance(ANALYSIS_62, "5.6.3").getProjectVersion();
		assertEquals("1.0.0-SNAPSHOT", ver62);
	}

}
