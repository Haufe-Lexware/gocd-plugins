package com.tw.go.task.dockerpipeline;

import com.tw.go.plugin.common.Context;

/**
 * Created by BradeaC on 13/04/2016.
 */
public class DockerRestartCommand extends DockerCommand
{
    public DockerRestartCommand(Context taskContext, Config taskConfig)
    {
        command.add("sudo");
        command.add("service");
        command.add("docker");
        command.add("restart");
    }
}
