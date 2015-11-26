package com.tw.go.plugin.common;

import com.google.gson.JsonArray;
import com.thoughtworks.go.plugin.api.task.JobConsoleLogger;
import com.thoughtworks.go.plugin.api.task.TaskExecutionContext;
import com.tw.go.plugin.common.mock.MockJobConsoleLogger;
import com.tw.go.plugin.common.mock.MockTaskExecutionContext;
import com.tw.go.plugin.common.utils.Environment;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.*;
import org.junit.rules.ExpectedException;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.security.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Properties;

import static org.junit.Assert.*;

/**
 * Created by MarkusW on 18.11.2015.
 */
public class GoApiClientTest {
    private Context context;
    private Map envVars;
    GoApiClient client;

    @Before
    public void init() throws Exception{

        Properties props = new Properties();
        InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream("test.properties");
        props.load(in);

        context = Environment.getDefaultContext(props);
        envVars = context.getEnvironmentVariables();
        client = new GoApiClient(context.getEnvironmentVariables().get("GO_SERVER_URL").toString());
    }

    @Rule
    public final ExpectedException exception = ExpectedException.none();

    @Test
    public void testGetJobProperty() throws Exception {
        // just read some properties from plugin test pipeline
        Assert.assertEquals("1", getJobProperty("cruise_pipeline_counter"));

        exception.expect(FileNotFoundException.class);
        getJobProperty("cruise_pipeline_counter_bla");
    }

    @Test
    public void testSetJobProperty() throws Exception {

        String newPropertyName = "MyTestProperty_" + System.currentTimeMillis();
        SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//dd/MM/yyyy
        Date now = new Date();
        String strDate = sdfDate.format(now);

        setJobProperty(newPropertyName, strDate);
        Assert.assertEquals(strDate, getJobProperty(newPropertyName));

        // if we set a new value later, the original should be returned, because overwriting is not allowed
        setJobProperty(newPropertyName, "Hello, I try to overwrite");
        Assert.assertEquals(strDate, getJobProperty(newPropertyName));
    }

    @Test
    public void testGetJobProperties() throws Exception {
        JSONObject obj = getJobProperties();
        Assert.assertTrue(10 < obj.length());

    }

    private String getJobProperty(String propertyName) throws Exception{
        return  client.getJobProperty(
                envVars.get("GO_PIPELINE_NAME").toString(),
                envVars.get("GO_PIPELINE_COUNTER").toString(),
                envVars.get("GO_STAGE_NAME").toString(),
                envVars.get("GO_STAGE_COUNTER").toString(),
                envVars.get("GO_JOB_NAME").toString(),
                propertyName);
    }

    private String setJobProperty(String propertyName, String propertyValue) throws Exception{
         return client.setJobProperty(
                envVars.get("GO_PIPELINE_NAME").toString(),
                envVars.get("GO_PIPELINE_COUNTER").toString(),
                envVars.get("GO_STAGE_NAME").toString(),
                envVars.get("GO_STAGE_COUNTER").toString(),
                envVars.get("GO_JOB_NAME").toString(),
                propertyName,
                propertyValue);
    }

    private JSONObject getJobProperties() throws Exception{
        return  client.getJobProperties(
                envVars.get("GO_PIPELINE_NAME").toString(),
                envVars.get("GO_PIPELINE_COUNTER").toString(),
                envVars.get("GO_STAGE_NAME").toString(),
                envVars.get("GO_STAGE_COUNTER").toString(),
                envVars.get("GO_JOB_NAME").toString());
    }
}