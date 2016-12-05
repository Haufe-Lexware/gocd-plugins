package com.tw.go.task.check_mk;

import com.thoughtworks.go.plugin.api.task.JobConsoleLogger;
import com.tw.go.plugin.common.Context;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.Map;

import static org.junit.Assert.assertEquals;

public class TaskExecutorFactoryTest
{
    static JobConsoleLogger consoleLogger;
    static Context context;
    static Map config;

    @BeforeClass
    public static void Init() throws Exception {
        CheckMkStub.init();
        config = Mockito.mock(Map.class);
        consoleLogger = Mockito.mock(JobConsoleLogger.class);
        context = Mockito.mock(Context.class);
    }


    @Test
    public void Create_AddAction_ReturnsAddHostObject() throws Exception, JobNotSupportedException {
        Map actionMock = Mockito.mock(Map.class);
        Mockito.when(actionMock.get("value")).thenReturn("add");
        Mockito.when(config.get(CheckMkTask.ACTION)).thenReturn(actionMock);

        CheckMkTaskExecutor taskExecutor= TaskExecutorFactory.Create(consoleLogger,context,config);

        assertEquals(taskExecutor.getClass(),AddHostTaskExecutor.class);
    }

    @Test
    public void Create_RemoveAction_ReturnsRemoveHostObject() throws Exception, JobNotSupportedException {
        Map actionMock = Mockito.mock(Map.class);
        Mockito.when(actionMock.get("value")).thenReturn("remove");
        Mockito.when(config.get(CheckMkTask.ACTION)).thenReturn(actionMock);

        CheckMkTaskExecutor taskExecutor= TaskExecutorFactory.Create(consoleLogger,context,config);

        assertEquals(taskExecutor.getClass(),RemoveHostTaskExecutor.class);
    }

    @Test
    public void Create_EditAction_ReturnsEditHostObject() throws Exception, JobNotSupportedException {
        Map actionMock = Mockito.mock(Map.class);
        Mockito.when(actionMock.get("value")).thenReturn("edit");
        Mockito.when(config.get(CheckMkTask.ACTION)).thenReturn(actionMock);

        CheckMkTaskExecutor taskExecutor= TaskExecutorFactory.Create(consoleLogger,context,config);

        assertEquals(taskExecutor.getClass(),EditHostTaskExecutor.class);
    }

    @Test(expected = JobNotSupportedException.class)
    public void Create_WrongAction_Throws() throws Exception, JobNotSupportedException {
        Map actionMock = Mockito.mock(Map.class);
        Mockito.when(actionMock.get("value")).thenReturn("test");
        Mockito.when(config.get(CheckMkTask.ACTION)).thenReturn(actionMock);

        CheckMkTaskExecutor taskExecutor= TaskExecutorFactory.Create(consoleLogger,context,config);

        assertEquals(taskExecutor.getClass(),RemoveHostTaskExecutor.class);
    }
}
