package com.tw.go.plugin.common;

import com.sun.xml.internal.ws.policy.sourcemodel.AssertionData;
import com.thoughtworks.go.plugin.api.task.TaskExecutionContext;
import com.thoughtworks.go.plugin.api.task.JobConsoleLogger;
import com.tw.go.plugin.common.mock.MockJobConsoleLogger;
import com.tw.go.plugin.common.mock.MockTaskExecutionContext;
import com.tw.go.plugin.common.utils.Environment;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import com.thoughtworks.go.plugin.api.task.TaskConfig;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.*;

/**
 * Created by MarkusW on 09.11.2015.
 */
public class EnvVarParamParserTest {
    private Context context;
    private TaskExecutionContext mockExecutionContext;
    private JobConsoleLogger mockConsole;

    @Before
    public void init() throws Exception{
        context = Environment.getDefaultContext();
        mockExecutionContext = new MockTaskExecutionContext(context.getEnvironmentVariables());
        mockConsole = new MockJobConsoleLogger(mockExecutionContext);
    }

    @Test
         public void testOneEnvironmentVarInParameter() throws Exception {
        EnvVarParamParser envParser = new EnvVarParamParser(context.getEnvironmentVariables(), mockConsole);

        Assert.assertEquals("123", envParser.Parse("${GO_PIPELINE_COUNTER}"));
        Assert.assertEquals("abc123", envParser.Parse("abc${GO_PIPELINE_COUNTER}"));
        Assert.assertEquals("123abc", envParser.Parse("${GO_PIPELINE_COUNTER}abc"));
        Assert.assertEquals("abc123abc", envParser.Parse("abc${GO_PIPELINE_COUNTER}abc"));
    }

    @Test
    public void testTwoEnvironmentVarsInParameter() throws Exception {
        EnvVarParamParser envParser = new EnvVarParamParser(context.getEnvironmentVariables(), mockConsole);

        Assert.assertEquals("123test-stage", envParser.Parse("${GO_PIPELINE_COUNTER}${GO_STAGE_NAME}"));
        Assert.assertEquals("123_test-stage", envParser.Parse("${GO_PIPELINE_COUNTER}_${GO_STAGE_NAME}"));
        Assert.assertEquals("abc123_test-stage", envParser.Parse("abc${GO_PIPELINE_COUNTER}_${GO_STAGE_NAME}"));
        Assert.assertEquals("123_test-stageabc", envParser.Parse("${GO_PIPELINE_COUNTER}_${GO_STAGE_NAME}abc"));
        Assert.assertEquals("abc123_test-stageabc", envParser.Parse("abc${GO_PIPELINE_COUNTER}_${GO_STAGE_NAME}abc"));
    }

    @Test
    public void testWithoutEnvironmentVarInParameter() throws Exception {
        EnvVarParamParser envParser = new EnvVarParamParser(context.getEnvironmentVariables(), mockConsole);

        Assert.assertEquals("",         envParser.Parse(""));
        Assert.assertEquals("abc",      envParser.Parse("abc"));
        Assert.assertEquals("$abc",     envParser.Parse("$abc"));
        Assert.assertEquals("${abc",    envParser.Parse("${abc"));
        Assert.assertEquals("$abc}",    envParser.Parse("$abc}"));
        Assert.assertEquals("{abc}",    envParser.Parse("{abc}"));
        Assert.assertEquals("a$bc",     envParser.Parse("a$bc"));
        Assert.assertEquals("a${bc",    envParser.Parse("a${bc"));
        Assert.assertEquals("a$b}c",    envParser.Parse("a$b}c"));
        Assert.assertEquals("a{b}c",    envParser.Parse("a{b}c"));

    }

    @Test
    public void testUnavailableEnvironmentVarInParameter() throws Exception {
        EnvVarParamParser envParser = new EnvVarParamParser(context.getEnvironmentVariables(), mockConsole);

        Assert.assertEquals("GO_UNAVAILABLE", envParser.Parse("${GO_UNAVAILABLE}"));


    }

}