package com.tw.go.task.dockerpipeline;

import com.tw.go.plugin.common.Context;

/**
 * Created by BradeaC on 21/12/2015.
 */
public class DockerPushCommand extends DockerCommand
{
    protected static String imgAndTag;

    public DockerPushCommand(Context taskContext, Config taskConfig)
    {
        super(taskContext, taskConfig);
    }

    @Override
    protected void buildCommand(Context taskContext, Config taskConfig)
    {
        command.add("docker");
        command.add("push");
        command.add(imgAndTag);
    }

    protected String getCommand()
    {
        return command.toString();
    }
}
