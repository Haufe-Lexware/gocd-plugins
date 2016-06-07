package com.tw.go.task.check_mk;

import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class RemoveHostJobTests {

    @BeforeClass
    public static void Init() throws Exception {
        CheckMkStub.init();

    }

    @Test(expected = RemoveHostException.class)
    public void Execute_RequestFailed_ThrowException() throws Exception {
        CheckMkStub.SetupRequestError();
        CheckMkJob job = new RemoveHostJob(CheckMkStub.checkMkClient);

        job.Execute(CheckMkRequestObjectFactory.CreateRemoveHostObject(CheckMkStub.hostName));
    }

    @Test
    public void Execute_RequestSuccess_ReturnsOkMessage() throws Exception {
        CheckMkStub.SetupRequestOk();
        CheckMkJob job = new RemoveHostJob(CheckMkStub.checkMkClient);

        String result=job.Execute(CheckMkRequestObjectFactory.CreateRemoveHostObject(CheckMkStub.hostName));

        assertEquals(result,"Host test removed");
    }
}


