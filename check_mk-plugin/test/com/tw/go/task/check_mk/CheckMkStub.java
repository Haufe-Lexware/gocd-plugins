package com.tw.go.task.check_mk;

import org.mockito.Mockito;

import java.io.InputStream;
import java.util.Properties;

public class CheckMkStub {

    public static String hostName;
    public static String hostIp;
    public static String folderPath;
    public static String userName;
    public static String password;
    public static String server;
    public static ICheckMkClient checkMkClient;

    public static void init() throws Exception {

        Properties props = new Properties();
        InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream("test.properties");
        props.load(in);

        CheckMkStub.hostName = props.getProperty("hostName");
        CheckMkStub.hostIp = props.getProperty("serverIp");
        CheckMkStub.folderPath = props.getProperty("folderPath");
        CheckMkStub.userName = props.getProperty("userName");
        CheckMkStub.password = props.getProperty("password");
        CheckMkStub.server = props.getProperty("server");

        checkMkClient = Mockito.mock(ICheckMkClient.class);

    }

    public static void SetupRequestError() throws Exception {
        Mockito.when(checkMkClient.ExecuteJob(Mockito.anyString(), Mockito.anyString())).thenReturn("{\"result\": null, \"result_code\": 1}");
    }

    public static void SetupRequestOk() throws Exception {
        Mockito.when(checkMkClient.ExecuteJob(Mockito.anyString(), Mockito.anyString())).thenReturn("{\"result\": null, \"result_code\": 0}");
    }
}


