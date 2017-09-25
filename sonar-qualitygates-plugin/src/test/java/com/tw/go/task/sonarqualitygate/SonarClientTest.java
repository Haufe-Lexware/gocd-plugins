package com.tw.go.task.sonarqualitygate;

import com.tw.go.task.sonarqualitygate.model.ProjectStatus;
import com.tw.go.task.sonarqualitygate.model.Sonar;
import com.tw.go.task.sonarqualitygate.model.SonarStatus;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import java.io.InputStream;
import java.util.Properties;

/**
 * Created by MarkusW on 26.10.2015.
 */
@Ignore
public class SonarClientTest {

    // properites required for executing the tests
    private static String sonarApiUrl;
    private static String sonarProjectKey;


    @BeforeClass
    public static void init() throws Exception {

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
        JSONObject qgDetails = parser.GetQualityGateDetails();

        String qgResult = qgDetails.getString("level");
        Assert.assertEquals("ERROR", qgResult);
    }

    @Test
    public void testQualityCheckSonar6x() throws Exception {
        final SonarClient sonarClient = new SonarClient(sonarApiUrl);

        final Sonar response = sonarClient.getSonarQualityStatus(sonarProjectKey);

        final SonarStatus projectStatus = response.getProjectStatus();

        Assert.assertEquals(ProjectStatus.ERROR, projectStatus.getStatus());
    }
}