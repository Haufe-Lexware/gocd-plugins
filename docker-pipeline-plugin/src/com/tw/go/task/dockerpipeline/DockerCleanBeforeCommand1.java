package com.tw.go.task.dockerpipeline;

import com.tw.go.plugin.common.Context;

/**
 * Created by BradeaC on 08/02/2016.
 */
public class DockerCleanBeforeCommand1 extends DockerCommand
{
    public DockerCleanBeforeCommand1(Context taskContext, Config taskConfig)
    {
        super(taskContext, taskConfig);
    }

    @Override
    protected void buildCommand(Context taskContext, Config taskConfig)
    {
        command.add("service");
        command.add("docker");
        command.add("restart");
    }
}
