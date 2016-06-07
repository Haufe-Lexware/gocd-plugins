package com.tw.go.plugin.common;

import com.tw.go.plugin.common.utils.Environment;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Properties;

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
        client.setBasicAuthentication(props.get("GO_USER").toString(), props.get("GO_PASSWORD").toString());
    }

    @Rule
    public final ExpectedException exception = ExpectedException.none();

    @Test
    public void convert() throws Exception {
        String test = "1234.0";
        float f = new Float(test);
        System.out.println(f);
        int i = (int)f;
        System.out.println(i);
    }

    @Test
    public void testGetJobProperty() throws Exception {
        // just read some properties from plugin test pipeline
        Assert.assertEquals("1", getJobProperty("cruise_pipeline_counter"));

        exception.expect(FileNotFoundException.class);
        getJobProperty("cruise_pipeline_counter_bla");
    }

    @Test
    public void maskPwd() {
        String s = "download https://theuser:blablubb@bitbucket.org/haufegroup/dummy-container.git to me.";
        s = s.replaceAll("(?<=://)[^:@]+(:[^@]+)?(?=@)","*******");
        System.out.println(s);
        }

    @Test
    public void testSetJobProperty() throws Exception {

        String newPropertyName = "MyTestProperty_" + System.currentTimeMillis();
        SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//dd/MM/yyyy
        Date now = new Date();
        String strDate = sdfDate.format(now);

        setJobProperty(newPropertyName, strDate);
        Assert.assertEquals(strDate, getJobProperty(newPropertyName));


        exception.expect(IOException.class);
        setJobProperty(newPropertyName, "Hello, I try to overwrite");
    }

    @Test
    public void testGetJobProperties() throws Exception {
        Map obj = getJobProperties();
        Assert.assertTrue(10 < obj.size());

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

    private Map getJobProperties() throws Exception{
        return  client.getJobProperties(
                envVars.get("GO_PIPELINE_NAME").toString(),
                envVars.get("GO_PIPELINE_COUNTER").toString(),
                envVars.get("GO_STAGE_NAME").toString(),
                envVars.get("GO_STAGE_COUNTER").toString(),
                envVars.get("GO_JOB_NAME").toString());
    }
}