package com.tw.go.task.dockerpipeline;

import com.tw.go.plugin.common.Context;

/**
 * Created by BradeaC on 08/02/2016.
 */
public class DockerCleanBefore extends DockerMultipleCommand
{
    public DockerCleanBefore(Context taskContext, Config taskConfig)
    {
        super(taskContext, taskConfig);
    }

    @Override
    protected void setupCommands(Context taskContext, Config taskConfig)
    {
        runCommand(new DockerRestartCommand(taskContext, taskConfig))
                .then(new DockerStopContainers(taskContext, taskConfig))
                .then(new DockerRemoveAllContainers(taskContext, taskConfig))
                .then(new DockerRemoveAllImages(taskContext, taskConfig));
    }


}
