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
    protected void command(Context taskContext, Config taskConfig)
    {
        String username = taskConfig.username;

        if ("".equals(taskConfig.username))
        {
            username = taskConfig.registryUsername;
        }

        if ("".equals(taskConfig.dockerFileName))
        {
            command.add("docker");
            command.add("build");
            command.add("-t");

            command.add(getRegistryName(taskConfig.registryUrlForLogin) + "/" + username + "/" + taskConfig.imageName + ":" + taskConfig.imageTag1);
            imageAndTag.add(getRegistryName(taskConfig.registryUrlForLogin) + "/" + username + "/" + taskConfig.imageName + ":" + taskConfig.imageTag1);

            if (!("".equals(taskConfig.imageTag2)))
            {
                command.add("-t");
                command.add(getRegistryName(taskConfig.registryUrlForLogin) + "/" + username + "/" + taskConfig.imageName + ":" + taskConfig.imageTag2);

                imageAndTag.add(getRegistryName(taskConfig.registryUrlForLogin) + "/" + username + "/" + taskConfig.imageName + ":" + taskConfig.imageTag2);
            }
            if (!("".equals(taskConfig.imageTag3)))
            {
                command.add("-t");
                command.add(getRegistryName(taskConfig.registryUrlForLogin) + "/" + username + "/" + taskConfig.imageName + ":" + taskConfig.imageTag3);

                imageAndTag.add(getRegistryName(taskConfig.registryUrlForLogin) + "/" + username + "/" + taskConfig.imageName + ":" + taskConfig.imageTag3);
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

            command.add(tag + getRegistryName(taskConfig.registryUrlForLogin) + "/" + username + "/" + taskConfig.imageName + ":" + taskConfig.imageTag1);
            imageAndTag.add(getRegistryName(taskConfig.registryUrlForLogin) + "/" + username + "/" + taskConfig.imageName + ":" + taskConfig.imageTag1);

            if (!("".equals(taskConfig.imageTag2)))
            {
                command.add(tag + getRegistryName(taskConfig.registryUrlForLogin) + "/" + username + "/" + taskConfig.imageName + ":" + taskConfig.imageTag2);
                imageAndTag.add(getRegistryName(taskConfig.registryUrlForLogin) + "/" + username + "/" + taskConfig.imageName + ":" + taskConfig.imageTag2);
            }
            if (!("".equals(taskConfig.imageTag3)))
            {
                command.add(tag + getRegistryName(taskConfig.registryUrlForLogin) + "/" + username + "/" + taskConfig.imageName + ":" + taskConfig.imageTag3);
                imageAndTag.add(getRegistryName(taskConfig.registryUrlForLogin) + "/" + username + "/" + taskConfig.imageName + ":" + taskConfig.imageTag3);
            }
            command.add("--file=" + taskContext.getWorkingDir() + "/" + taskConfig.dockerFileName);
            command.add(taskContext.getWorkingDir() + "/" + dockerPath);
        }
    }

    protected String getCommand()
    {
        return command.toString();
    }

    protected String getRegistryName(String registryAddress)
    {
        String result;
        String[] split = registryAddress.split("/");

        if (split.length <= 2)
        {
            result = split[0];
        }
        else
        {
            result = split[2];
        }

        return result;
    }
}
