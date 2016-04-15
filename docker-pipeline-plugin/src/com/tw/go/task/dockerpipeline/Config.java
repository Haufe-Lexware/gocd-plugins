package com.tw.go.task.dockerpipeline;

import java.util.Map;

/**
 * Created by BradeaC on 14/12/2015.
 */
public class Config
{
    public final boolean isDockerClean;
    public final String imageName;
    public final String dockerFileName;
    public final String buildArgs;
    public final String username;
    public final String imageTag;
    public final String registryUsername;
    public final String registryPassword;
    public final String registryEmail;
    public final String registryUrlForLogin;
    public final boolean isDockerCleanAfter;

    public Config (Map config)
    {
        isDockerClean = "true".equals(getValue(config, DockerTask.IS_DOCKER_CLEAN));
        imageName = getValue(config, DockerTask.IMAGE_NAME);
        dockerFileName = getValue(config, DockerTask.DOCKER_FILE_NAME);
        buildArgs = getValue(config, DockerTask.BUILD_ARGS);

        username = getValue(config, DockerTask.USERNAME);
        imageTag = getValue(config, DockerTask.IMAGE_TAG);

        registryUsername = getValue(config, DockerTask.REGISTRY_USERNAME);
        registryPassword = getValue(config, DockerTask.REGISTRY_PASSWORD);
        registryEmail = getValue(config, DockerTask.REGISTRY_EMAIL);
        registryUrlForLogin = getValue(config, DockerTask.REGISTRY_URL_FOR_LOGIN);

        isDockerCleanAfter = "true".equals(getValue(config, DockerTask.IS_DOCKER_CLEAN_AFTER));
    }

    private static String getValue(Map config, String property)
    {
        return (String) ((Map) config.get(property)).get("value");
    }


}
