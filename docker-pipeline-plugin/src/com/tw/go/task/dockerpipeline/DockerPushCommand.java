package com.tw.go.task.dockerpipeline;

import com.tw.go.plugin.common.Context;

/**
 * Created by BradeaC on 21/12/2015.
 */
public class DockerPushCommand extends DockerCommand
{
    public DockerPushCommand(Context taskContext, Config taskConfig, String tag)
    {
        command.add("docker");
        command.add("push");
        command.add(tag);
    }
}
