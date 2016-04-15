package com.tw.go.task.dockerpipeline;

import com.tw.go.plugin.common.Context;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.regex.Pattern;

/**
 * Created by BradeaC on 21/12/2015.
 */
public class DockerBuildCommand extends DockerCommand
{
    public DockerBuildCommand(Context taskContext, Config taskConfig)
    {
        String context = getDockerfilePath(taskContext, taskConfig).getParent().toString(); // the path without the Dockerfile. Used for obtaining the build context
        String dockerfilePath = getDockerfileName(taskContext, taskConfig); // the path including the Dockerfile. Used for pointing to the Dockerfile
        String username = getUsername(taskConfig);
        String registryName = getRegistryName(taskConfig.registryUrlForLogin);
        String baseImageName = makeBaseName(registryName, username, taskConfig.imageName);

        command.add("docker");
        command.add("build");

        command.add("-f");
        command.add(dockerfilePath);

        addBuildArgs(taskConfig.buildArgs);

        addImageTag(baseImageName, taskConfig.imageTag);

        command.add(context);
    }

    private void addBuildArgs(String buildArgs)
    {
        for (String buildArg : splitByFirstOrDefault(buildArgs, ';'))
        {
            if(!buildArg.isEmpty())
            {
                command.add("--build-arg");
                command.add(buildArg);
            }
        }
    }

    private void addImageTag(String baseName, String tags)
    {
        for (String tag : splitByFirstOrDefault(tags, ';'))
        {
            if(!tag.isEmpty())
            {
                String taggedName = baseName + ":" + tag;
                command.add("-t");
                command.add(taggedName);
                imageAndTag.add(taggedName);
            }
        }
    }

    private String[] splitByFirstOrDefault(String args, Character separator)
    {
        if(!args.isEmpty())
        {
            Character first = args.charAt(0);

            if(!(Character.isLetter(first)))
            {
                separator = first;
                args = args.substring(1);
            }

            return args.split(Pattern.quote(separator.toString()));
        }

        return new String[]{};
    }

    private String makeBaseName(String registry, String username, String imageName)
    {
        return registry + "/" + username + imageName;
    }

    private String getDockerfileName(Context taskContext, Config taskConfig)
    {
        String result = taskConfig.dockerFileName.isEmpty() ?
                taskContext.getWorkingDir() + "/" + "Dockerfile" :
                taskContext.getWorkingDir() + "/" + taskConfig.dockerFileName;

        return result;
    }

    private String getUsername(Config taskConfig)
    {
        if(!taskConfig.registryUsername.isEmpty())
        {
            return taskConfig.username.isEmpty() ? taskConfig.registryUsername + "/" : taskConfig.username + "/";
        }

        return "";
    }

    private Path getDockerfilePath(Context taskContext, Config taskConfig)
    {
        Path dockerPath = Paths.get(getDockerfileName(taskContext, taskConfig)).toAbsolutePath().normalize();

        return dockerPath;
    }

    private String getRegistryName(String registryAddress)
    {
        String[] split = registryAddress.split("/");

        String result = split.length <= 2 ? split[0] : split[2];

        return result;
    }
}
