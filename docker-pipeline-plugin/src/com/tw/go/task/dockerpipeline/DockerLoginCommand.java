package com.tw.go.task.dockerpipeline;

import com.tw.go.plugin.common.Context;

public class DockerLoginCommand extends DockerCommand
{
    public DockerLoginCommand(Context taskContext, Config taskConfig)
    {
        add("docker");
        add("login");
        add("--username=" + taskConfig.registryUsername);
        add("--password=" + taskConfig.registryPassword);

        if(!(taskConfig.registryEmail.isEmpty()))
        {
            add("--email=" + taskConfig.registryEmail);
        }

        add(taskConfig.registryUrlForLogin);
    }
}