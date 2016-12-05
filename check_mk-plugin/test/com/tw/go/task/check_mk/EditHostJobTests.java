package com.tw.go.task.check_mk;

import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
public class EditHostJobTests {

    @BeforeClass
    public static void Init() throws Exception {
        CheckMkStub.init();

    }

    @Test(expected = EditHostException.class)
    public void Execute_RequestFailed_ThrowException() throws Exception {
        CheckMkStub.SetupRequestError();
        CheckMkJob job = new EditHostJob(CheckMkStub.checkMkClient);

        job.Execute(CheckMkRequestObjectFactory.CreateEditHostObject(CheckMkStub.folderPath, CheckMkStub.hostName, CheckMkStub.hostIp));
    }

    @Test
    public void Execute_RequestSuccess_ReturnsOkMessage() throws Exception {
        CheckMkStub.SetupRequestOk();
        CheckMkJob job = new EditHostJob(CheckMkStub.checkMkClient);

        String result = job.Execute(CheckMkRequestObjectFactory.CreateEditHostObject(CheckMkStub.folderPath, CheckMkStub.hostName, CheckMkStub.hostIp));

        assertEquals(result, "Host test updated.");
    }
}

