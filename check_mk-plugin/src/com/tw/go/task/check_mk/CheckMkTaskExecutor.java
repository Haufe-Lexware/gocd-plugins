package com.tw.go.task.check_mk;

import com.thoughtworks.go.plugin.api.task.JobConsoleLogger;
import com.tw.go.plugin.common.Context;
import com.tw.go.plugin.common.TaskExecutor;
import org.json.JSONObject;

import java.util.Map;

public abstract class CheckMkTaskExecutor extends TaskExecutor {
    protected ICheckMkClient checkMkClient;

    public CheckMkTaskExecutor(JobConsoleLogger console, Context context, Map config) {
        super(console, context, config);
    }

    protected String getPluginLogPrefix() {
        return "[Check_Mk Task] ";
    }

    public void setCheckMkClient(ICheckMkClient checkMkClient) {
        this.checkMkClient = checkMkClient;
    }

    protected void TryInitClient(ServerCredentials credentials) throws Exception {
        if(checkMkClient==null)
        {
            checkMkClient=new CheckMkClient(credentials.getServer(),credentials.getUsername(),credentials.getPassword());
        }
    }

    protected ServerCredentials CreateCredentials()
    {
        String server = (String) ((Map) this.config.get(CheckMkTask.CHECK_MK_SERVER)).get("value");
        String username = (String) ((Map) this.config.get(CheckMkTask.USERNAME)).get("value");
        String password = (String) ((Map) this.config.get(CheckMkTask.PASSWORD)).get("value");
        return new ServerCredentials(username,password,server);
    }

    protected void ExecuteJob(JSONObject requestObject, CheckMkJob job) throws Exception {
        log(job.Execute(requestObject));
        job=new ActivateChangesJob(checkMkClient);
        log(job.Execute(null));
    }
}
