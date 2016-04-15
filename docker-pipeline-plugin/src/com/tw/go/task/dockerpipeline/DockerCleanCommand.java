package com.tw.go.task.dockerpipeline;

import com.tw.go.plugin.common.Context;

/**
 * Created by BradeaC on 21/12/2015.
 */
public class DockerCleanCommand extends DockerCommand
{
    public DockerCleanCommand(Context taskContext, Config taskConfig)
    {
        command.add("/bin/sh");
        command.add("-c");
        command.add("test -n \"$(docker images -qa)\" && docker rmi -f $(docker images -qa) || echo \"No images to delete\"");
    }
}
