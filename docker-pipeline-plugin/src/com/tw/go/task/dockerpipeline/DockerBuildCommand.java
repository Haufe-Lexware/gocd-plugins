package com.tw.go.task.dockerpipeline;

import com.tw.go.plugin.common.Context;

/**
 * Created by BradeaC on 21/12/2015.
 */
public class DockerBuildCommand extends DockerCommand
{
    static String tag = "--tag=";

    public DockerBuildCommand(Context taskContext, Config taskConfig)
    {
        super(taskContext,taskConfig);
    }

    @Override
    protected void buildCommand(Context taskContext, Config taskConfig)
    {
        if ("".equals(taskConfig.dockerFileName))
        {
            command.add("docker");
            command.add("build");
            command.add("-t");

            command.add(taskConfig.registryURL + "/" + taskConfig.username + "/" + taskConfig.imageName + ":" + taskConfig.imageTag1);
            imageAndTag.add(taskConfig.registryURL + "/" + taskConfig.username + "/" + taskConfig.imageName + ":" + taskConfig.imageTag1);

            if (!("".equals(taskConfig.imageTag2)))
            {
                command.add("-t");
                command.add(taskConfig.registryURL + "/" + taskConfig.username + "/" + taskConfig.imageName + ":" + taskConfig.imageTag2);

                imageAndTag.add(taskConfig.registryURL + "/" + taskConfig.username + "/" + taskConfig.imageName + ":" + taskConfig.imageTag2);
            }
            if (!("".equals(taskConfig.imageTag3)))
            {
                command.add("-t");
                command.add(taskConfig.registryURL + "/" + taskConfig.username + "/" + taskConfig.imageName + ":" + taskConfig.imageTag3);

                imageAndTag.add(taskConfig.registryURL + "/" + taskConfig.username + "/" + taskConfig.imageName + ":" + taskConfig.imageTag3);
            }
            command.add("/var/lib/go-agent/" + taskContext.getWorkingDir());
        }
        else
        {
            String dockerPath = "";

            try
            {
                dockerPath = taskConfig.dockerFileName.substring(0, taskConfig.dockerFileName.lastIndexOf("/"));
            }
            catch (Exception e)
            {
                if (e != null)
                {
                    dockerPath = "";
                }
            }

            command.add("docker");
            command.add("build");

            command.add(tag + taskConfig.registryURL + "/" + taskConfig.username + "/" + taskConfig.imageName + ":" + taskConfig.imageTag1);
            imageAndTag.add(taskConfig.registryURL + "/" + taskConfig.username + "/" + taskConfig.imageName + ":" + taskConfig.imageTag1);

            if (!("".equals(taskConfig.imageTag2)))
            {
                command.add(tag + taskConfig.registryURL + "/" + taskConfig.username + "/" + taskConfig.imageName + ":" + taskConfig.imageTag2);
                imageAndTag.add(taskConfig.registryURL + "/" + taskConfig.username + "/" + taskConfig.imageName + ":" + taskConfig.imageTag2);
            }
            if (!("".equals(taskConfig.imageTag3)))
            {
                command.add(tag + taskConfig.registryURL + "/" + taskConfig.username + "/" + taskConfig.imageName + ":" + taskConfig.imageTag3);
                imageAndTag.add(taskConfig.registryURL + "/" + taskConfig.username + "/" + taskConfig.imageName + ":" + taskConfig.imageTag3);
            }
            command.add("--file=" + taskContext.getWorkingDir() + "/" + taskConfig.dockerFileName);
            command.add(taskContext.getWorkingDir() + "/" + dockerPath);
        }
    }

    protected String getCommand()
    {
        return command.toString();
    }
}
