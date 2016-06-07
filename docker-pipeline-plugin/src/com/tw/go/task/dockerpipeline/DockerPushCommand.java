package com.tw.go.task.dockerpipeline;

import com.thoughtworks.go.plugin.api.task.JobConsoleLogger;
import com.tw.go.plugin.common.AbstractCommand;
import com.tw.go.plugin.common.ConfigVars;

public class DockerPushCommand extends AbstractCommand
{
    public DockerPushCommand(JobConsoleLogger console, ConfigVars configVars, String tag)
    {
        super(console);

        command.add("docker");
        command.add("push");
        command.add(tag);
    }
}
