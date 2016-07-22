package com.tw.go.task.dockercompose;

import com.thoughtworks.go.plugin.api.logging.Logger;
import com.thoughtworks.go.plugin.api.task.JobConsoleLogger;
import com.tw.go.plugin.common.Context;
import com.tw.go.plugin.common.Result;
import com.tw.go.plugin.common.TaskExecutor;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class DockerComposeTaskExecutor extends TaskExecutor {
    private final Logger logger = Logger.getLoggerFor(DockerComposeTaskExecutor.class);

    public DockerComposeTaskExecutor(JobConsoleLogger console, Context context, Map config) {
        super(console, context, config);
    }

    @Override
    protected String getPluginLogPrefix() {
        return "[Docker Compose] ";
    }

    // String pipelineName, String pipelineCounter, String stageName, String stageCounter, String jobName, String propertyName, String propertyValue

    @Override
    public Result execute() throws Exception {
        try {
            if (configVars.isChecked(DockerComposeTask.COMPOSE_REMOVE_VOLUMES)) {
                new DockerComposeStopCommand(console, configVars)
                        .run();
                new DockerComposeRmCommand(console, configVars)
                        .run();
            }
            if (configVars.isChecked(DockerComposeTask.FORCE_PULL)) {
                new DockerComposePullCommand(console, configVars)
                        .run();
            }
            if (configVars.isChecked(DockerComposeTask.FORCE_BUILD)) {
                new DockerComposeBuildCommand(console, configVars)
                        .run();
            }
            if (!configVars.isChecked(DockerComposeTask.FORCE_BUILD_ONLY)) {
                new DockerComposeUpCommand(console, configVars)
                        .run();
            }
        } catch (Exception e) {
            logException(logger, e);
            return new Result(false, getPluginLogPrefix() + e.getMessage(), e);
        }

        return new Result(true, "Finished");
    }
}