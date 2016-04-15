package com.tw.go.task.dockerpipeline;

import com.tw.go.plugin.common.Context;

/**
 * Created by BradeaC on 21/12/2015.
 */
public class DockerLoginCommand extends DockerCommand
{
    public DockerLoginCommand(Context taskContext, Config taskConfig)
    {
        command.add("docker");
        command.add("login");
        command.add("--username=" + taskConfig.registryUsername);
        command.add("--password=" + taskConfig.registryPassword);

        //if(!(taskConfig.registryEmail.isEmpty()))
        //{
            command.add("--email=" + taskConfig.registryEmail);
        //}

        command.add(taskConfig.registryUrlForLogin);
    }
}
