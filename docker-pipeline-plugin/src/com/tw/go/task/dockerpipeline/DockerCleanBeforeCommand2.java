package com.tw.go.task.dockerpipeline;

import com.tw.go.plugin.common.Context;

/**
 * Created by BradeaC on 08/02/2016.
 */
public class DockerCleanBeforeCommand2 extends DockerCommand
{
    public DockerCleanBeforeCommand2(Context taskContext, Config taskConfig)
    {
        super(taskContext, taskConfig);
    }

    @Override
    protected void buildCommand(Context taskContext, Config taskConfig)
    {
        command.add("/bin/sh");
        command.add("-c");
        command.add("test -n \"$(docker ps -a -q)\" && docker stop $(docker ps -a -q) || echo \"No containers to stop\"");
    }
}
