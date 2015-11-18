package com.tw.go.plugin.common;

import com.sun.corba.se.spi.orb.PropertyParser;
import com.thoughtworks.go.plugin.api.task.JobConsoleLogger;
import com.thoughtworks.go.plugin.api.task.TaskExecutionContext;
import com.tw.go.plugin.common.mock.MockJobConsoleLogger;
import com.tw.go.plugin.common.mock.MockTaskExecutionContext;
import com.tw.go.plugin.common.utils.Environment;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Map;

/**
 * Created by MarkusW on 09.11.2015.
 */
public class JobPropParamParserTest {
    private Context context;
    private TaskExecutionContext mockExecutionContext;
    private JobConsoleLogger mockConsole;

    private String testPropertyValue1;
    private String testPropertyValue2;

    @Before
    public void init() throws Exception{
        context = Environment.getDefaultContext();
        mockExecutionContext = new MockTaskExecutionContext(context.getEnvironmentVariables());
        mockConsole = new MockJobConsoleLogger(mockExecutionContext);

        // initialize test properties
        testPropertyValue1 = setJobProperty("test_property_1", "Test Value 1");
        testPropertyValue2 = setJobProperty("test_property_2", "Test Value 2");

    }



    @Test
    public void testOnePropVarInParameter() throws Exception {
        JobPropParamParser parser = new JobPropParamParser(context.getEnvironmentVariables(), mockConsole);

        Assert.assertEquals("Test Value 1", parser.Parse("%{test_property_1}"));
        Assert.assertEquals("abcTest Value 1", parser.Parse("abc%{test_property_1}"));
        Assert.assertEquals("Test Value 1abc", parser.Parse("%{test_property_1}abc"));
        Assert.assertEquals("abcTest Value 1abc", parser.Parse("abc%{test_property_1}abc"));
    }

    @Test
    public void testTwoPropVarsInParameter() throws Exception {
        JobPropParamParser propParser = new JobPropParamParser(context.getEnvironmentVariables(), mockConsole);

        Assert.assertEquals("Test Value 1Test Value 2", propParser.Parse("%{test_property_1}%{test_property_2}"));
        Assert.assertEquals("Test Value 1_Test Value 2", propParser.Parse("%{test_property_1}_%{test_property_2}"));
        Assert.assertEquals("abcTest Value 1_Test Value 2", propParser.Parse("abc%{test_property_1}_%{test_property_2}"));
        Assert.assertEquals("Test Value 1_Test Value 2abc", propParser.Parse("%{test_property_1}_%{test_property_2}abc"));
        Assert.assertEquals("abcTest Value 1_Test Value 2abc", propParser.Parse("abc%{test_property_1}_%{test_property_2}abc"));
    }

    @Test
    public void testPropVarAndEnvVarInParameter() throws Exception {
        EnvVarParamParser envParser = new EnvVarParamParser(context.getEnvironmentVariables(), mockConsole);
        JobPropParamParser propParser = new JobPropParamParser(context.getEnvironmentVariables(), mockConsole);

        Assert.assertEquals("Test Value 1TestPluginStage", propParser.Parse(envParser.Parse("%{test_property_1}${GO_STAGE_NAME}")));
        Assert.assertEquals("Test Value 1_TestPluginStage", propParser.Parse(envParser.Parse("%{test_property_1}_${GO_STAGE_NAME}")));
        Assert.assertEquals("abcTest Value 1_TestPluginStage", propParser.Parse(envParser.Parse("abc%{test_property_1}_${GO_STAGE_NAME}")));
        Assert.assertEquals("Test Value 1_TestPluginStageabc", propParser.Parse(envParser.Parse("%{test_property_1}_${GO_STAGE_NAME}abc")));
        Assert.assertEquals("abcTest Value 1_TestPluginStageabc", propParser.Parse(envParser.Parse("abc%{test_property_1}_${GO_STAGE_NAME}abc")));
    }

    @Test
    public void testWithoutPropVarInParameter() throws Exception {
        JobPropParamParser propParser = new JobPropParamParser(context.getEnvironmentVariables(), mockConsole);

        Assert.assertEquals("",         propParser.Parse(""));
        Assert.assertEquals("abc",      propParser.Parse("abc"));
        Assert.assertEquals("$abc",     propParser.Parse("$abc"));
        Assert.assertEquals("${abc",    propParser.Parse("${abc"));
        Assert.assertEquals("$abc}",    propParser.Parse("$abc}"));
        Assert.assertEquals("{abc}",    propParser.Parse("{abc}"));
        Assert.assertEquals("a$bc",     propParser.Parse("a$bc"));
        Assert.assertEquals("a${bc",    propParser.Parse("a${bc"));
        Assert.assertEquals("a$b}c",    propParser.Parse("a$b}c"));
        Assert.assertEquals("a{b}c",    propParser.Parse("a{b}c"));

    }

    @Test
    public void testUnavailableEnvironmentVarInParameter() throws Exception {
        JobPropParamParser propParser = new JobPropParamParser(context.getEnvironmentVariables(), mockConsole);

        Assert.assertEquals("GO_UNAVAILABLE", propParser.Parse("%{GO_UNAVAILABLE}"));


    }


    private String setJobProperty(String propertyName, String propertyValue) throws Exception{
        GoApiClient client = new GoApiClient(context.getEnvironmentVariables().get("GO_SERVER_URL").toString());
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