package com.tw.go.task.sonarqualitygate;

import static org.junit.Assert.assertFalse;

import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Logger;

import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.tw.go.task.sonarqualitygate.enumeration.EndpointSonarEnum;

/**
 * Created by MarkusW on 26.10.2015.
 */

public class SonarClientTest {

	// properites required for executing the tests
	private String sonarApiUrl; 
	private String sonarProjectKey;

	@Before
	public void init() throws Exception {

		// init from properites file (this is sonar installation specific.
		Properties props = new Properties();
		InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream("test.properties");
		props.load(in);

		// api properites
		sonarApiUrl = props.getProperty("sonarApiUrl");
		sonarProjectKey = props.getProperty("sonarProjectKey");
	}

	@Test
	public void testQualityGateResult() throws Exception {

		// create a sonar client
		SonarClient sonarClient = new SonarClient(this.sonarApiUrl);

		// get quality gate details
		JSONObject result = sonarClient.getProjectWithQualityGateDetails(this.sonarProjectKey);

		// check that a quality gate is returned

		String qgResult = SonarParser.getInstance(result).getProjectQualityGateStatus();
		Assert.assertEquals("OK", qgResult);
	}

	@Test
	public void testSonarVersion() throws Exception {

		SonarClient sonarClient = new SonarClient(sonarApiUrl);
		String version = sonarClient.getSonarVersion();
		Logger.getLogger(SonarParserTest.class.getName()).info("Version: " + version);
		assertFalse(EndpointSonarEnum.isSonarVersionUntil62(version));
	}

}