package com.tw.go.task.dockerpipeline;

import com.tw.go.plugin.common.Context;

public class DockerCleanBefore extends DockerMultipleCommand
{
    public DockerCleanBefore(Context taskContext, Config taskConfig)
    {
        runCommand(new DockerRestartCommand(taskContext, taskConfig))
                .then(new DockerStopContainers(taskContext, taskConfig))
                .then(new DockerRemoveAllContainers(taskContext, taskConfig))
                .then(new DockerRemoveAllImages(taskContext, taskConfig));
    }
}
