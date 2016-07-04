package com.tw.go.task.dockerpipeline;

import com.thoughtworks.go.plugin.api.task.JobConsoleLogger;
import com.tw.go.plugin.common.ConfigVars;

/**
 * Created by BradeaC on 21/12/2015.
 */
public class DockerPushCommand extends DockerCommand
{
    public DockerPushCommand(JobConsoleLogger console, ConfigVars configVars, String tag)
    {
        super(console, configVars);
        command.add("docker");
        command.add("push");
        command.add(tag);
    }
}
