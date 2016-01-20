package com.tw.go.task.dockerpipeline;

import com.tw.go.plugin.common.Context;

/**
 * Created by BradeaC on 21/12/2015.
 */
public class DockerTagCommand extends DockerCommand
{
    public DockerTagCommand(Context taskContext, Config taskConfig)
    {
        super(taskContext, taskConfig);
    }

    @Override
    protected void buildCommand(Context taskContext, Config taskConfig)
    {
        command.add("docker");
        command.add("tag");
        command.add("-f");
        command.add(taskConfig.registryURL + "/" + taskConfig.imageName);
        command.add(taskConfig.registryURL + "/" + taskConfig.username + "/" + taskConfig.imageName + ":" + taskConfig.imageTag);
    }

    protected String getCommand()
    {
        return command.toString();
    }
}
