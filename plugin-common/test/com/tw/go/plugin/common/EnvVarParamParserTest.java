package com.tw.go.plugin.common;

import com.thoughtworks.go.plugin.api.task.TaskExecutionContext;
import com.thoughtworks.go.plugin.api.task.JobConsoleLogger;
import com.tw.go.plugin.common.mock.MockJobConsoleLogger;
import com.tw.go.plugin.common.mock.MockTaskExecutionContext;
import com.tw.go.plugin.common.utils.Environment;
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

//    private TaskConfig config;
    private Context context;
    private TaskExecutionContext mockExecutionContext;
    private JobConsoleLogger mockConsole;


    @Before
    public void init() throws Exception{
//        config = mock(TaskConfig.class);

        context = Environment.getDefaultContext();
        mockExecutionContext = new MockTaskExecutionContext(context.getEnvironmentVariables());
        mockConsole = new MockJobConsoleLogger(mockExecutionContext);

    }


    @Test
    public void testParse() throws Exception {

        EnvVarParamParser envParser = new EnvVarParamParser(context.getEnvironmentVariables(), mockConsole);
    }

}