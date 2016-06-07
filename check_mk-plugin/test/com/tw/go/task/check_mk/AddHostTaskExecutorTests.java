package com.tw.go.task.check_mk;

import com.thoughtworks.go.plugin.api.response.DefaultGoApiResponse;
import com.thoughtworks.go.plugin.api.task.JobConsoleLogger;
import com.tw.go.plugin.common.Context;
import com.tw.go.plugin.common.Result;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.Map;

import static org.junit.Assert.assertEquals;

public class AddHostTaskExecutorTests {
    static JobConsoleLogger consoleLogger;
    static Context context;
    static Map config;

    @BeforeClass
    public static void Init() throws Exception {
        CheckMkStub.init();
        config = Mockito.mock(Map.class);
        consoleLogger = Mockito.mock(JobConsoleLogger.class);
        context = Mockito.mock(Context.class);
        Map serverMock = Mockito.mock(Map.class);
        Mockito.when(serverMock.get("value")).thenReturn(CheckMkStub.server);
        Mockito.when(config.get(CheckMkTask.CHECK_MK_SERVER)).thenReturn(serverMock);

        Map hostnameMock = Mockito.mock(Map.class);
        Mockito.when(hostnameMock.get("value")).thenReturn(CheckMkStub.hostName);
        Mockito.when(config.get(CheckMkTask.HOSTNAME)).thenReturn(hostnameMock);

        Map hostIpMock = Mockito.mock(Map.class);
        Mockito.when(hostIpMock.get("value")).thenReturn(CheckMkStub.hostIp);
        Mockito.when(config.get(CheckMkTask.HOST_IP)).thenReturn(hostIpMock);

        Map folderPathMock = Mockito.mock(Map.class);
        Mockito.when(folderPathMock.get("value")).thenReturn(CheckMkStub.folderPath);
        Mockito.when(config.get(CheckMkTask.FOLDER_PATH)).thenReturn(folderPathMock);

        Map userNameMock = Mockito.mock(Map.class);
        Mockito.when(userNameMock.get("value")).thenReturn(CheckMkStub.userName);
        Mockito.when(config.get(CheckMkTask.USERNAME)).thenReturn(userNameMock);

        Map passwordMock = Mockito.mock(Map.class);
        Mockito.when(passwordMock.get("value")).thenReturn(CheckMkStub.password);
        Mockito.when(config.get(CheckMkTask.PASSWORD)).thenReturn(passwordMock);
    }

    @Test
    public void Execute_OneRequestFailed_ReturnFalseResult() throws Exception {
        CheckMkStub.SetupRequestError();
        AddHostTaskExecutor taskExecutor = new AddHostTaskExecutor(consoleLogger, context, config);
        taskExecutor.setCheckMkClient(CheckMkStub.checkMkClient);
        Result result = taskExecutor.execute();

        assertEquals(result.responseCode(), DefaultGoApiResponse.INTERNAL_ERROR);
    }

    @Test
    public void Execute_RequestSuccess_ReturnsOkMessage() throws Exception {
        CheckMkStub.SetupRequestOk();
        AddHostTaskExecutor taskExecutor = new AddHostTaskExecutor(consoleLogger, context, config);
        taskExecutor.setCheckMkClient(CheckMkStub.checkMkClient);
        Result result = taskExecutor.execute();

        assertEquals(result.responseCode(), DefaultGoApiResponse.SUCCESS_RESPONSE_CODE);
    }
}

