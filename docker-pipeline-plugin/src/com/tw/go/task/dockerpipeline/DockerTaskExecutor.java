package com.tw.go.task.dockerpipeline;

import com.thoughtworks.go.plugin.api.logging.Logger;
import com.thoughtworks.go.plugin.api.task.JobConsoleLogger;
import com.tw.go.plugin.common.Context;
import com.tw.go.plugin.common.MaskingJobConsoleLogger;
import com.tw.go.plugin.common.Result;
import com.tw.go.plugin.common.TaskExecutor;

import java.util.Map;

public class DockerTaskExecutor extends TaskExecutor {
    private static final Logger logger = Logger.getLoggerFor(DockerTaskExecutor.class);
    public DockerTaskExecutor(JobConsoleLogger console, Context context, Map config) {
        super(console, context, config);
    }

    public Result execute() throws Exception {
        try {
            return runCommand();
        } catch (Exception e) {
            return new Result(false, getPluginLogPrefix() + e.getMessage(), e);
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
            if (!configVars.isEmpty(DockerTask.REGISTRY_USERNAME) && !configVars.isEmpty(DockerTask.REGISTRY_PASSWORD)
                    && !configVars.isEmpty(DockerTask.REGISTRY_URL)) {
                new DockerLoginCommand(console, configVars)
                        .run();
            }

            if (!configVars.isEmpty(DockerTask.IMAGE_TAG)) {
                DockerBuildCommand build = new DockerBuildCommand(console, configVars);
                build.run();

                if (!configVars.isEmpty(DockerTask.REGISTRY_URL)) {
                    for (String tag : build.imageAndTag) {
                        if (tag != null) {
                            new DockerPushCommand(console, configVars, tag)
                                    .run();
                        }
                    }
                }
            }

            if (!configVars.isEmpty(DockerTask.RUN_IMAGE)) {
                new DockerRunCommand(console, configVars)
                        .run();
            }
        } finally {
            if (configVars.isChecked(DockerTask.CLEAN_AFTER_COMPLETE)) {
                new DockerStopContainers(console, configVars)
                        .run();
                new DockerRemoveAllContainers(console, configVars)
                        .run();
                new DockerRemoveAllImages(console, configVars)
                        .run();
            }

            if (configVars.isChecked(DockerTask.CLEAN_AFTER_TASK)) {
                // http://blog.yohanliyanage.com/2015/05/docker-clean-up-after-yourself/
                new DockerRemoveExitedContainers(console, configVars)
                        .run();
                new DockerRemoveDanglingImages(console, configVars)
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
