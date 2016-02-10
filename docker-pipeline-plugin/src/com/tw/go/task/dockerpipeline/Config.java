package com.tw.go.task.dockerpipeline;

import javax.print.Doc;
import java.util.Map;

/**
 * Created by BradeaC on 14/12/2015.
 */
public class Config
{
    public final boolean isDockerClean;
    public final String registryURL;
    public final String imageName;
    public final String dockerFileName;
    public final String username;
    public final String imageTag1;
    public final String imageTag2;
    public final String imageTag3;
    public final String registryUsername;
    public final String registryPassword;
    public final String registryEmail;
    public final String registryUrlForLogin;

    public Config (Map config)
    {
        isDockerClean = getValue(config, DockerTask.IS_DOCKER_CLEAN).equals("true");
        registryURL = getValue(config, DockerTask.REGISTRY_URL);
        imageName = getValue(config, DockerTask.IMAGE_NAME);
        dockerFileName = getValue(config, DockerTask.DOCKER_FILE_NAME);

        username = getValue(config, DockerTask.USERNAME);
        imageTag1 = getValue(config, DockerTask.IMAGE_TAG1);
        imageTag2 = getValue(config, DockerTask.IMAGE_TAG2);
        imageTag3 = getValue(config, DockerTask.IMAGE_TAG3);

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
