
package com.tw.go.task.check_mk;


import com.thoughtworks.go.plugin.api.task.JobConsoleLogger;
import com.tw.go.plugin.common.Context;
import com.tw.go.plugin.common.Result;
import org.json.JSONObject;

import java.util.Map;

public class AddHostTaskExecutor extends CheckMkTaskExecutor {

    public AddHostTaskExecutor(JobConsoleLogger console, Context context, Map config) {
        super(console, context, config);
    }

    public Result execute() throws Exception {
        try {
            String hostname = (String) ((Map) this.config.get(CheckMkTask.HOSTNAME)).get("value");
            String serverIp = (String) ((Map) this.config.get(CheckMkTask.HOST_IP)).get("value");
            String folderPath = (String) ((Map) this.config.get(CheckMkTask.FOLDER_PATH)).get("value");
            ServerCredentials credentials = CreateCredentials();
            TryInitClient(credentials);
            CheckMkJob checkMkJob = new AddHostJob(checkMkClient);
            JSONObject requestObject = CheckMkRequestObjectFactory.CreateAddHostObject(folderPath, hostname, serverIp);
            ExecuteJob(requestObject, checkMkJob);
            return new Result(true, "Add host was successful");
        } catch (Exception e) {
            log("Exception. " + e.getMessage());
            return new Result(false, "Add host failed", e);
        }
    }

    protected String getPluginLogPrefix() {
        return "[Add Host Task] ";
    }

    public void setCheckMkClient(ICheckMkClient checkMkClient) {
        this.checkMkClient = checkMkClient;
    }
}

