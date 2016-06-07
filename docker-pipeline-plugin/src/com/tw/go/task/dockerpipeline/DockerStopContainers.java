package com.tw.go.task.dockerpipeline;

import com.thoughtworks.go.plugin.api.task.JobConsoleLogger;
import com.tw.go.plugin.common.AbstractCommand;
import com.tw.go.plugin.common.ConfigVars;
import com.tw.go.plugin.common.Context;

public class DockerStopContainers extends AbstractCommand
{
    public DockerStopContainers(JobConsoleLogger console, ConfigVars configVars)
    {
        super(console);

        add("/bin/sh");
        add("-c");
        add("test -n \"$(docker ps -a -q)\" && docker stop $(docker ps -a -q) || echo \"No containers to stop\"");
    }
}
