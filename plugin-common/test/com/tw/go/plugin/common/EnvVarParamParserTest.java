package com.tw.go.plugin.common;

import com.thoughtworks.go.plugin.api.task.TaskExecutionContext;
import com.thoughtworks.go.plugin.api.task.JobConsoleLogger;
import com.tw.go.plugin.common.mock.MockJobConsoleLogger;
import com.tw.go.plugin.common.mock.MockTaskExecutionContext;
import com.tw.go.plugin.common.utils.Environment;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.InputStream;
import java.util.Properties;

/**
 * Created by MarkusW on 09.11.2015.
 */
public class EnvVarParamParserTest {
    private Context context;
    private TaskExecutionContext mockExecutionContext;
    private JobConsoleLogger mockConsole;

    @Before
    public void init() throws Exception{

        Properties props = new Properties();
        InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream("test.properties");
        props.load(in);

        context = Environment.getDefaultContext(props);
        mockExecutionContext = new MockTaskExecutionContext(context.getEnvironmentVariables());
        mockConsole = new MockJobConsoleLogger(mockExecutionContext);
    }

    @Test
    public void testOneEnvironmentVarInParameter() throws Exception {
        EnvVarParamParser envParser = new EnvVarParamParser(context.getEnvironmentVariables(), mockConsole);

        Assert.assertEquals("1", envParser.parse("${GO_PIPELINE_COUNTER}"));
        Assert.assertEquals("abc1", envParser.parse("abc${GO_PIPELINE_COUNTER}"));
        Assert.assertEquals("1abc", envParser.parse("${GO_PIPELINE_COUNTER}abc"));
        Assert.assertEquals("abc1abc", envParser.parse("abc${GO_PIPELINE_COUNTER}abc"));
    }

    @Test
    public void testTwoEnvironmentVarsInParameter() throws Exception {
        EnvVarParamParser envParser = new EnvVarParamParser(context.getEnvironmentVariables(), mockConsole);

        Assert.assertEquals("1TestPluginStage", envParser.parse("${GO_PIPELINE_COUNTER}${GO_STAGE_NAME}"));
        Assert.assertEquals("1_TestPluginStage", envParser.parse("${GO_PIPELINE_COUNTER}_${GO_STAGE_NAME}"));
        Assert.assertEquals("abc1_TestPluginStage", envParser.parse("abc${GO_PIPELINE_COUNTER}_${GO_STAGE_NAME}"));
        Assert.assertEquals("1_TestPluginStageabc", envParser.parse("${GO_PIPELINE_COUNTER}_${GO_STAGE_NAME}abc"));
        Assert.assertEquals("abc1_TestPluginStageabc", envParser.parse("abc${GO_PIPELINE_COUNTER}_${GO_STAGE_NAME}abc"));
    }

    @Test
    public void testWithoutEnvironmentVarInParameter() throws Exception {
        EnvVarParamParser envParser = new EnvVarParamParser(context.getEnvironmentVariables(), mockConsole);

        Assert.assertEquals("",         envParser.parse(""));
        Assert.assertEquals("abc",      envParser.parse("abc"));
        Assert.assertEquals("$abc",     envParser.parse("$abc"));
        Assert.assertEquals("${abc",    envParser.parse("${abc"));
        Assert.assertEquals("$abc}",    envParser.parse("$abc}"));
        Assert.assertEquals("{abc}",    envParser.parse("{abc}"));
        Assert.assertEquals("a$bc",     envParser.parse("a$bc"));
        Assert.assertEquals("a${bc",    envParser.parse("a${bc"));
        Assert.assertEquals("a$b}c",    envParser.parse("a$b}c"));
        Assert.assertEquals("a{b}c",    envParser.parse("a{b}c"));

    }

    @Test
    public void testUnavailableEnvironmentVarInParameter() throws Exception {
        EnvVarParamParser envParser = new EnvVarParamParser(context.getEnvironmentVariables(), mockConsole);

        Assert.assertEquals("GO_UNAVAILABLE", envParser.parse("${GO_UNAVAILABLE}"));

    }

}