package com.tw.go.task.dockerpipeline;

import com.tw.go.plugin.common.Context;

/**
 * Created by BradeaC on 13/04/2016.
 */
public class DockerRemoveAllImages extends DockerCommand
{
    public DockerRemoveAllImages(Context taskContext, Config taskConfig)
    {
        super(taskContext, taskConfig);
    }

    @Override
    protected void command(Context taskContext, Config taskConfig)
    {
        command.add("/bin/sh");
        command.add("-c");
        command.add("test -n \"$(docker images -q)\" && docker rmi -f $(docker images -q) || echo \"No images to delete\"");
    }
}
