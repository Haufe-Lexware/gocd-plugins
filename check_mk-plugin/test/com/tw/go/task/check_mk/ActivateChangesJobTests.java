package com.tw.go.task.check_mk;

import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ActivateChangesJobTests {

    @BeforeClass
    public static void Init() throws Exception {
        CheckMkStub.init();

    }

    @Test(expected = ActivateChangesException.class)
    public void Execute_RequestFailed_ThrowException() throws Exception {
        CheckMkStub.SetupRequestError();
        CheckMkJob job = new ActivateChangesJob(CheckMkStub.checkMkClient);

        job.Execute(null);
    }

    @Test
    public void Execute_RequestSuccess_ReturnsOkMessage() throws Exception {
        CheckMkStub.SetupRequestOk();
        CheckMkJob job = new ActivateChangesJob(CheckMkStub.checkMkClient);

        String result=job.Execute(null);

        assertEquals(result,"Changes activated");
    }
}

