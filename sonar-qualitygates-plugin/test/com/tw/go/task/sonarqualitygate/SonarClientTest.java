package com.tw.go.task.sonarqualitygate;

import org.json.JSONObject;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.InputStream;
import java.util.Properties;

import static junit.framework.TestCase.assertEquals;

/**
 * Created by MarkusW on 26.10.2015.
 */
public class SonarClientTest {

    // properites required for executing the tests
    private static String sonarApiUrl;
    private static String sonarProjectKey;


    @BeforeClass
    public static void init() throws Exception{

        // init from properites file (this is sonar installation specific.
        Properties props = new Properties();
        InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream("test.properties");
        props.load(in);

        // api properites
        SonarClientTest.sonarApiUrl = props.getProperty("sonarApiUrl");
        SonarClientTest.sonarProjectKey = props.getProperty("sonarProjectKey");
    }

    @Test
    public void testQualityGateResult() throws Exception {

        // create a sonar client
        SonarClient sonarClient = new SonarClient(this.sonarApiUrl);

        // get quality gate details
        JSONObject result = sonarClient.getProjectWithQualityGateDetails(this.sonarProjectKey);

        SonarParser parser = new SonarParser(result);

        // check that a quality gate is returned
        String qgResult = parser.getProjectQualityGateStatus();
        assertEquals("OK", qgResult);

        assertEquals("2018-01-26T12:32:07+0000", parser.getDate());
    }

}