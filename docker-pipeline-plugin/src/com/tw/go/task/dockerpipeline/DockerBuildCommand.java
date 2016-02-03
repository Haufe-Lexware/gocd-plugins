package com.tw.go.task.dockerpipeline;

import com.thoughtworks.go.plugin.api.task.JobConsoleLogger;
import com.tw.go.plugin.common.Context;

import java.io.Console;
import java.util.List;
import java.util.Map;

/**
 * Created by BradeaC on 21/12/2015.
 */
public class DockerBuildCommand extends DockerCommand
{
    public DockerBuildCommand(Context taskContext, Config taskConfig)
    {
        super(taskContext,taskConfig);
    }

    @Override
    protected void buildCommand(Context taskContext, Config taskConfig)
    {
        if (taskConfig.dockerFileName.equals(""))
        {
            command.add("docker");
            command.add("build");
            command.add("-t");
            command.add(taskConfig.registryURL + "/" + taskConfig.imageName);
            command.add("/var/lib/go-agent/" + taskContext.getWorkingDir());
        }
        else
        {
            command.add("docker");
            command.add("build");
            command.add("--tag=" + taskConfig.registryURL + "/" + taskConfig.imageName);
            command.add("--file=" + taskContext.getWorkingDir() + "/" + taskConfig.dockerFileName);
            command.add(taskContext.getWorkingDir());
        }
    }

    protected String getCommand()
    {
        return command.toString();
    }
}
