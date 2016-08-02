package com.tw.go.task.dockerpipeline;

import com.thoughtworks.go.plugin.api.task.JobConsoleLogger;
import com.tw.go.plugin.common.*;

import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;


public class DockerBuildCommand extends DockerCommand {
    protected List<String> imageAndTag = new ArrayList<>();

    public DockerBuildCommand(JobConsoleLogger console, ConfigVars configVars) {
        super(console, configVars);
        String dockerfileAbsolutePath = getDockerfileAbsolutePath(configVars); // the path including the Dockerfile. Used for pointing to the Dockerfile
        String context = getDirectory(dockerfileAbsolutePath); // the path without the Dockerfile. Used for obtaining the build context

        String username = getUsername(configVars);
        String registryName = getRegistryName(configVars.getValue(DockerTask.REGISTRY_URL_FOR_LOGIN));
        String baseImageName = makeBaseName(registryName, username, configVars.getValue(DockerTask.IMAGE_NAME));

        add("docker");
        add("build");

        // tbd: why had this been commented out?
        // race conditions? obsolete amount of work?
        add("--pull=true");
        add("--force-rm");

        add("-f");
        add(dockerfileAbsolutePath);

        if (configVars.isChecked(DockerTask.BUILD_NO_CACHE)) {
            add("--no-cache");
        }

        addBuildArgs(configVars.getValue(DockerTask.BUILD_ARGS));

        addImageTag(baseImageName, configVars.getValue(DockerTask.IMAGE_TAG), configVars.getValue(DockerTask.IMAGE_TAG_POSTFIX));

        add(context);
    }

    private void addBuildArgs(String buildArgs) {
        for (String buildArg : ListUtil.splitByFirstOrDefault(buildArgs, ';')) {
            if (!buildArg.isEmpty()) {
                command.add("--build-arg");
                command.add(buildArg);
            }
        }
    }

    private void addImageTag(String baseName, String tags, String tagPostfix) {
        for (String tag : ListUtil.splitByFirstOrDefault(tags, ';')) {
            if (!tag.isEmpty()) {
                if (!tagPostfix.isEmpty()) {
                    tag += "-" + tagPostfix;
                }
                String taggedName = baseName + ":" + tag;
                command.add("-t");
                command.add(taggedName);
                imageAndTag.add(taggedName);
            }
        }
    }

    private String makeBaseName(String registry, String username, String imageName) {
        if (null != registry && !registry.isEmpty()) {
            if ((registry.indexOf("docker.io") < 0) &&
                (registry.indexOf("hub.docker.com") < 0)) {
                return registry + "/" + username + "/" + imageName;
            } else {
                // Official docker hub; leave out registry, please
                return username + "/" + imageName;
            }
        }
        // Assume local name
        return imageName;
    }

    private String getDockerfileAbsolutePath(ConfigVars configVars) {
        String dockerfile = configVars.isEmpty(DockerTask.DOCKER_FILE_NAME) ? "Dockerfile" : configVars.getValue(DockerTask.DOCKER_FILE_NAME);

        return Paths.get(getAbsoluteWorkingDir() + "/" + dockerfile)
                .toAbsolutePath()
                .normalize()
                .toString();
    }

    private String getUsername(ConfigVars configVars) {
        if (!configVars.isEmpty(DockerTask.USERNAME)) {
            return configVars.getValue(DockerTask.USERNAME);
        }

        return configVars.getValue(DockerTask.REGISTRY_USERNAME);
    }

    private String getDirectory(String dockerFile) {
        return Paths.get(dockerFile).getParent().toString();
    }

    private String getRegistryName(String registryAddress) {
        try {
            if (-1 == registryAddress.indexOf("://")) {
                registryAddress = "http://" + registryAddress;
            }

            return new URL(registryAddress).getAuthority();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return "";
    }

}
