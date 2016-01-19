package com.tw.go.task.check_mk;

import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
public class AddHostJobTests {

    @BeforeClass
    public static void Init() throws Exception {
        CheckMkStub.init();

    }

    @Test(expected = AddHostException.class)
    public void Execute_RequestFailed_ThrowException() throws Exception {
        CheckMkStub.SetupRequestError();
        CheckMkJob job = new AddHostJob(CheckMkStub.checkMkClient);

        job.Execute(CheckMkRequestObjectFactory.CreateAddHostObject(CheckMkStub.folderPath, CheckMkStub.hostName, CheckMkStub.hostIp));
    }

    @Test
    public void Execute_RequestSuccess_ReturnsOkMessage() throws Exception {
        CheckMkStub.SetupRequestOk();
        CheckMkJob job = new AddHostJob(CheckMkStub.checkMkClient);

        String result = job.Execute(CheckMkRequestObjectFactory.CreateAddHostObject(CheckMkStub.folderPath, CheckMkStub.hostName, CheckMkStub.hostIp));

        assertEquals(result, "Host test added");
    }
}

