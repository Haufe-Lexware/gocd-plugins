package com.tw.go.task.dockerpipeline;

import com.thoughtworks.go.plugin.api.task.JobConsoleLogger;
import com.tw.go.plugin.common.Context;
import com.tw.go.plugin.common.Result;
import com.tw.go.plugin.common.TaskExecutor;


import java.util.Map;

public class DockerTaskExecutor extends TaskExecutor {
    public DockerTaskExecutor(JobConsoleLogger console, Context context, Map config) {
        super(console, context, config);
    }

    public Result execute() {
        try {
            return runCommand();
        } catch (Exception e) {
            return new Result(false, "Failed while running the task", e);
        }
    }

    public Result runCommand() throws Exception {
        try {
            if(configVars.isChecked(DockerTask.CLEAN_BEFORE_TASK)) {
                new DockerRestartCommand(console, configVars)
                        .run();
                new DockerStopContainers(console, configVars)
                        .run();
                new DockerRemoveAllContainers(console, configVars)
                        .run();
                new DockerRemoveAllImages(console, configVars)
                        .run();
            }

            if (!configVars.isEmpty(DockerTask.REGISTRY_USERNAME) && !configVars.isEmpty(DockerTask.REGISTRY_PASSWORD)) {
                new DockerLoginCommand(console, configVars)
                        .run();
            }

            if (!configVars.isEmpty(DockerTask.IMAGE_TAG)) {
                DockerBuildCommand build = new DockerBuildCommand(console, configVars);
                build.run();

                if (!configVars.isEmpty(DockerTask.REGISTRY_URL_FOR_LOGIN)) {
                    for (String tag : build.imageAndTag) {
                        if (tag != null) {
                            new DockerPushCommand(console, configVars, tag)
                                    .run();
                        }
                    }
                }
            }
        } finally {
            if (configVars.isChecked(DockerTask.CLEAN_AFTER_TASK)) {
                new DockerRemoveAllContainers(console, configVars)
                        .run();
                new DockerRemoveAllImages(console, configVars)
                        .run();
            }
        }

        return new Result(true, "Finished");
    }

    @Override
    protected String getPluginLogPrefix() {
        return "[Docker] ";
    }
}