package com.tw.go.task.check_mk;

import com.thoughtworks.go.plugin.api.task.JobConsoleLogger;
import com.tw.go.plugin.common.Context;
import com.tw.go.plugin.common.Result;
import org.json.JSONObject;

import java.util.Map;

/**
 * Created by BocR on 14/01/2016.
 */
public class RemoveHostTaskExecutor extends CheckMkTaskExecutor {
    public RemoveHostTaskExecutor(JobConsoleLogger console, Context context, Map config) {
        super(console, context, config);
    }

    public Result execute() throws Exception {
        try {
            String hostname = (String) ((Map) this.config.get(CheckMkTask.HOSTNAME)).get("value");
            ServerCredentials credentials = CreateCredentials();
            TryInitClient(credentials);
            try {
                JSONObject requestObject = CheckMkRequestObjectFactory.CreateRemoveHostObject(hostname);
                CheckMkJob checkMkJob = new RemoveHostJob(checkMkClient);
                ExecuteJob(requestObject, checkMkJob);
            } catch (RemoveHostException e) {
                log(e.getMessage());
            }
            return new Result(true, "Remove host task was successful");
        } catch (Exception e) {
            log("Exception. " + e.getMessage());
            return new Result(false, "Remove host task failed", e);
        }
    }

    @Override
    protected String getPluginLogPrefix() {
        return "[Remove Host Task] ";
    }
}

