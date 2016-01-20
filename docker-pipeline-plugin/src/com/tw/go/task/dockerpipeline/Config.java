package com.tw.go.task.dockerpipeline;

import java.util.Map;

/**
 * Created by BradeaC on 14/12/2015.
 */
public class Config
{
    public final String registryURL;
    public final String imageName;
    public final String dockerFileName;
    public final String username;
    public final String imageTag;
    public final String registryUsername;
    public final String registryPassword;
    public final String registryEmail;
    public final String registryUrlForLogin;

    public Config (Map config)
    {
        registryURL = getValue(config, DockerTask.REGISTRY_URL);
        imageName = getValue(config, DockerTask.IMAGE_NAME);
        dockerFileName = getValue(config, DockerTask.DOCKER_FILE_NAME);

        username = getValue(config, DockerTask.USERNAME);
        imageTag = getValue(config, DockerTask.IMAGE_TAG);

        registryUsername = getValue(config, DockerTask.REGISTRY_USERNAME);
        registryPassword = getValue(config, DockerTask.REGISTRY_PASSWORD);
        registryEmail = getValue(config, DockerTask.REGISTRY_EMAIL);
        registryUrlForLogin = getValue(config, DockerTask.REGISTRY_URL_FOR_LOGIN);
    }

    private String getValue(Map config, String property)
    {
        return (String) ((Map) config.get(property)).get("value");
    }


}
