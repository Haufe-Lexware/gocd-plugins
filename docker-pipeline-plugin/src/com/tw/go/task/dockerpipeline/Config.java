package com.tw.go.task.dockerpipeline;

public class Config
{
    public final boolean cleanBeforeTask;
    public final boolean cleanAfterTask;
    public final String imageName;
    public final String dockerFileName;
    public final String buildArgs;
    public final String username;
    public final String imageTag;
    public final String registryUsername;
    public final String registryPassword;
    public final String registryEmail;
    public final String registryUrlForLogin;

    public Config (ConfigVars config)
    {
        cleanBeforeTask = Boolean.parseBoolean(config.getValue(DockerTask.CLEAN_BEFORE_TASK));

        imageName = config.getValue(DockerTask.IMAGE_NAME);
        dockerFileName = config.getValue(DockerTask.DOCKER_FILE_NAME);
        buildArgs = config.getValue(DockerTask.BUILD_ARGS);

        username = config.getValue(DockerTask.USERNAME);
        imageTag = config.getValue(DockerTask.IMAGE_TAG);

        registryUsername = config.getValue(DockerTask.REGISTRY_USERNAME);
        registryPassword = config.getValue(DockerTask.REGISTRY_PASSWORD);
        registryEmail = config.getValue(DockerTask.REGISTRY_EMAIL);
        registryUrlForLogin = config.getValue(DockerTask.REGISTRY_URL_FOR_LOGIN);

        cleanAfterTask = Boolean.parseBoolean(config.getValue(DockerTask.CLEAN_AFTER_TASK));
    }
}
