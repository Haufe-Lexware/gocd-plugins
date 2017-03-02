package com.tw.go.plugin.common;

import com.thoughtworks.go.plugin.api.task.JobConsoleLogger;
import com.thoughtworks.go.plugin.api.task.TaskExecutionContext;
import com.tw.go.plugin.common.mock.MockJobConsoleLogger;
import com.tw.go.plugin.common.mock.MockTaskExecutionContext;
import com.tw.go.plugin.common.utils.Environment;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;

/**
 * Created by MarkusW on 09.11.2015.
 */
public class JobPropParamParserTest {
    private Context context;
    private TaskExecutionContext mockExecutionContext;
    private JobConsoleLogger mockConsole;

    private String testPropertyValue1;
    private String testPropertyValue2;
    private String testPropertyValue3;

    @Before
    public void init() throws Exception{
        Properties props = new Properties();
        InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream("test.properties");
        props.load(in);

        context = Environment.getDefaultContext(props);
        mockExecutionContext = new MockTaskExecutionContext(context.getEnvironmentVariables());
        mockConsole = new MockJobConsoleLogger(mockExecutionContext);

        // initialize test properties (if not exists)
        try {
            testPropertyValue1 = setJobProperty("test_property_1", "Test Value 1");
            testPropertyValue2 = setJobProperty("test_property_2", "Test Value 2");
            testPropertyValue3 = setJobProperty("test_property_TestPluginStage", "Test Value 3");
        }
        catch (IOException e){
            System.out.println("Error: " + e);
        }

    }



//    @Test
//    public void testOnePropVarInParameter() throws Exception {
//        JobPropParamParser parser = new JobPropParamParser(context.getEnvironmentVariables(), mockConsole);
//
//        Assert.assertEquals("Test Value 1", parser.parse("%{test_property_1}"));
//        Assert.assertEquals("abcTest Value 1", parser.parse("abc%{test_property_1}"));
//        Assert.assertEquals("Test Value 1abc", parser.parse("%{test_property_1}abc"));
//        Assert.assertEquals("abcTest Value 1abc", parser.parse("abc%{test_property_1}abc"));
//    }

    @Test
    public void testTwoPropVarsInParameter() throws Exception {
        JobPropParamParser propParser = new JobPropParamParser(context.getEnvironmentVariables(), mockConsole);

        Assert.assertEquals("Test Value 1Test Value 2", propParser.parse("%{test_property_1}%{test_property_2}"));
        Assert.assertEquals("Test Value 1_Test Value 2", propParser.parse("%{test_property_1}_%{test_property_2}"));
        Assert.assertEquals("abcTest Value 1_Test Value 2", propParser.parse("abc%{test_property_1}_%{test_property_2}"));
        Assert.assertEquals("Test Value 1_Test Value 2abc", propParser.parse("%{test_property_1}_%{test_property_2}abc"));
        Assert.assertEquals("abcTest Value 1_Test Value 2abc", propParser.parse("abc%{test_property_1}_%{test_property_2}abc"));
    }

    @Test
    public void testPropVarAndEnvVarInParameter() throws Exception {
        EnvVarParamParser envParser = new EnvVarParamParser(context.getEnvironmentVariables(), mockConsole);
        JobPropParamParser propParser = new JobPropParamParser(context.getEnvironmentVariables(), mockConsole);

        Assert.assertEquals("Test Value 3", propParser.parse(envParser.parse("%{test_property_${GO_STAGE_NAME}}")));
        Assert.assertEquals("abcTest Value 3", propParser.parse(envParser.parse("abc%{test_property_${GO_STAGE_NAME}}")));
        Assert.assertEquals("Test Value 3abc", propParser.parse(envParser.parse("%{test_property_${GO_STAGE_NAME}}abc")));
        Assert.assertEquals("abcTest Value 3abc", propParser.parse(envParser.parse("abc%{test_property_${GO_STAGE_NAME}}abc")));
    }

    @Test
    public void testEnvVarAsPropVarNameInParameter() throws Exception {
        EnvVarParamParser envParser = new EnvVarParamParser(context.getEnvironmentVariables(), mockConsole);
        JobPropParamParser propParser = new JobPropParamParser(context.getEnvironmentVariables(), mockConsole);

        Assert.assertEquals("Test Value 1TestPluginStage", propParser.parse(envParser.parse("%{test_property_1}${GO_STAGE_NAME}")));
        Assert.assertEquals("Test Value 1_TestPluginStage", propParser.parse(envParser.parse("%{test_property_1}_${GO_STAGE_NAME}")));
        Assert.assertEquals("abcTest Value 1_TestPluginStage", propParser.parse(envParser.parse("abc%{test_property_1}_${GO_STAGE_NAME}")));
        Assert.assertEquals("Test Value 1_TestPluginStageabc", propParser.parse(envParser.parse("%{test_property_1}_${GO_STAGE_NAME}abc")));
        Assert.assertEquals("abcTest Value 1_TestPluginStageabc", propParser.parse(envParser.parse("abc%{test_property_1}_${GO_STAGE_NAME}abc")));
    }

    @Test
    public void testWithoutPropVarInParameter() throws Exception {
        JobPropParamParser propParser = new JobPropParamParser(context.getEnvironmentVariables(), mockConsole);

        Assert.assertEquals("",         propParser.parse(""));
        Assert.assertEquals("abc",      propParser.parse("abc"));
        Assert.assertEquals("$abc",     propParser.parse("$abc"));
        Assert.assertEquals("${abc",    propParser.parse("${abc"));
        Assert.assertEquals("$abc}",    propParser.parse("$abc}"));
        Assert.assertEquals("{abc}",    propParser.parse("{abc}"));
        Assert.assertEquals("a$bc",     propParser.parse("a$bc"));
        Assert.assertEquals("a${bc",    propParser.parse("a${bc"));
        Assert.assertEquals("a$b}c",    propParser.parse("a$b}c"));
        Assert.assertEquals("a{b}c",    propParser.parse("a{b}c"));

    }

    @Test
    public void testUnavailableEnvironmentVarInParameter() throws Exception {
        JobPropParamParser propParser = new JobPropParamParser(context.getEnvironmentVariables(), mockConsole);

        Assert.assertEquals("GO_UNAVAILABLE", propParser.parse("%{GO_UNAVAILABLE}"));


    }


    private String setJobProperty(String propertyName, String propertyValue) throws Exception{

        Properties props = new Properties();
        InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream("test.properties");
        props.load(in);

        GoApiClient client = new GoApiClient(context.getEnvironmentVariables().get("GO_SERVER_URL").toString());
        client.setBasicAuthentication(props.get("GO_USER").toString(), props.get("GO_PASSWORD").toString());

        Map envVars = context.getEnvironmentVariables();
        return client.setJobProperty(
                envVars.get("GO_PIPELINE_NAME").toString(),
                envVars.get("GO_PIPELINE_COUNTER").toString(),
                envVars.get("GO_STAGE_NAME").toString(),
                envVars.get("GO_STAGE_COUNTER").toString(),
                envVars.get("GO_JOB_NAME").toString(),
                propertyName,
                propertyValue);
    }
}