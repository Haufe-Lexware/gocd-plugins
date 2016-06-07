package com.tw.go.task.dockerpipeline;

import com.thoughtworks.go.plugin.api.task.JobConsoleLogger;
import com.tw.go.plugin.common.AbstractCommand;
import com.tw.go.plugin.common.ConfigVars;
import com.tw.go.plugin.common.GoApiConstants;
import com.tw.go.plugin.common.ListUtil;

import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class DockerBuildCommand extends AbstractCommand {
    protected List<String> imageAndTag = new ArrayList<>();

    public DockerBuildCommand(JobConsoleLogger console, ConfigVars configVars)
    {
        super(console);
        String dockerfileAbsolutePath = getDockerfileAbsolutePath(configVars); // the path including the Dockerfile. Used for pointing to the Dockerfile
        String context = getDirectory(dockerfileAbsolutePath); // the path without the Dockerfile. Used for obtaining the build context

        String username = getUsername(configVars);
        String registryName = getRegistryName(configVars.getValue(DockerTask.REGISTRY_URL_FOR_LOGIN));
        String baseImageName = makeBaseName(registryName, username, configVars.getValue(DockerTask.IMAGE_NAME));

        add("docker");
        add("build");

        add("-f");
        add(dockerfileAbsolutePath);

        addBuildArgs(configVars.getValue(DockerTask.BUILD_ARGS));

        addImageTag(baseImageName, configVars.getValue(DockerTask.IMAGE_TAG));

        add(context);
    }

    private void addBuildArgs(String buildArgs)
    {
        for (String buildArg : ListUtil.splitByFirstOrDefault(buildArgs, ';'))
        {
            if (!buildArg.isEmpty())
            {
                command.add("--build-arg");
                command.add(buildArg);
            }
        }
    }

    private void addImageTag(String baseName, String tags)
    {
        for (String tag : ListUtil.splitByFirstOrDefault(tags, ';'))
        {
            if (!tag.isEmpty())
            {
                String taggedName = baseName + ":" + tag;
                command.add("-t");
                command.add(taggedName);
                imageAndTag.add(taggedName);
            }
        }
    }

    private String makeBaseName(String registry, String username, String imageName)
    {
        return registry + "/" + username + "/" + imageName;
    }

    private String getDockerfileAbsolutePath(ConfigVars configVars)
    {
        String dockerfile = configVars.isEmpty(DockerTask.DOCKER_FILE_NAME) ?
                "Dockerfile" : configVars.getValue(DockerTask.DOCKER_FILE_NAME);

        return Paths.get(configVars.getValue(GoApiConstants.ENVVAR_NAME_GO_WORKING_DIR) + "/" + dockerfile)
                .toAbsolutePath()
                .normalize()
                .toString();
    }

    private String getUsername(ConfigVars configVars)
    {
        if (!configVars.isEmpty(DockerTask.USERNAME))
        {
            return configVars.getValue(DockerTask.USERNAME);
        }

        return configVars.getValue(DockerTask.REGISTRY_USERNAME);
    }

    private String getDirectory(String dockerFile)
    {
        return Paths.get(dockerFile).getParent().toString();
    }

    private String getRegistryName(String registryAddress)
    {
        try
        {
            if (-1 == registryAddress.indexOf("://"))
            {
                registryAddress = "http://" + registryAddress;
            }

            return new URL(registryAddress).getAuthority();
        }
        catch (MalformedURLException e)
        {
            e.printStackTrace();
        }

        return "";
    }
}