package com.tw.go.task.dockerpipeline;

import com.thoughtworks.go.plugin.api.task.JobConsoleLogger;
import com.tw.go.plugin.common.*;

import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.tw.go.plugin.common.GoApiConstants.ENVVAR_GO_REVISION;
import static com.tw.go.plugin.common.GoApiConstants.ENVVAR_NAME_GO_PIPELINE_COUNTER;
import static com.tw.go.plugin.common.GoApiConstants.ENVVAR_NAME_GO_PIPELINE_NAME;


public class DockerBuildCommand extends DockerCommand {
    protected List<String> imageAndTag = new ArrayList<>();
    protected String repositoryUrl = "";

    public DockerBuildCommand(JobConsoleLogger console, ConfigVars configVars, String repoUrl) {
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

        repositoryUrl = repoUrl;

        //console.printLine("repoUrl" + repositoryUrl);

        addBuildArgs(configVars);

        addImageTag(baseImageName, configVars.getValue(DockerTask.IMAGE_TAG), configVars.getValue(DockerTask.IMAGE_TAG_POSTFIX));

        add(context);
    }

    public void addBuildArgs(ConfigVars configVars) {

        String buildArgs = configVars.getValue(DockerTask.BUILD_ARGS);

        if (configVars.isChecked(DockerTask.BUILD_MICROLABELING)) {
            for (String microlabel : ListUtil.splitByFirstOrDefault(buildMicroLabeling(configVars), ';')) {
                if (!microlabel.isEmpty()) {
                    command.add("--build-arg");
                    command.add(microlabel);
                }
            }
        }

        for (String buildArg : ListUtil.splitByFirstOrDefault(buildArgs, ';')) {
            if (!buildArg.isEmpty()) {
                command.add("--build-arg");
                command.add(buildArg);
            }
        }
    }

    private String buildMicroLabeling(ConfigVars configVars) {
        String BUILD_VENDOR = "BUILD_VENDOR=";
        String BUILD_NAME = "BUILD_NAME=";
        String BUILD_VERSION = "BUILD_VERSION=";
        String BUILD_DATE = "BUILD_DATE=";
        String REPOSITORY_URL = "REPOSITORY_URL=";
        String REPOSITORY_REF = "REPOSITORY_REF=";

        Date dt = new Date();
        SimpleDateFormat date_format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String timestamp = date_format.format(dt);

        BUILD_VENDOR += "Haufe-Lexware" + ";";
        BUILD_NAME += configVars.getValue(DockerTask.IMAGE_NAME) + ";";
        BUILD_VERSION += configVars.getValue(ENVVAR_NAME_GO_PIPELINE_COUNTER) + ";";
        BUILD_DATE += timestamp + ";";
        REPOSITORY_URL += repositoryUrl + ";";
        REPOSITORY_REF += configVars.getValue(ENVVAR_GO_REVISION) + ";";

        return BUILD_VENDOR + BUILD_NAME +
                BUILD_VERSION + BUILD_DATE +
                REPOSITORY_URL + REPOSITORY_REF;
    }

    public void addImageTag(String baseName, String tags, String tagPostfix) {
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

    public String makeBaseName(String registry, String username, String imageName) {
        String result = imageName;

        if (!username.isEmpty()) {
            result = username + "/" + result;
        }

        if (!registry.isEmpty()) {
            result = registry + "/" + result;
        }

        return result;
    }

    public String getDockerfileAbsolutePath(ConfigVars configVars) {
        String dockerfile = configVars.isEmpty(DockerTask.DOCKER_FILE_NAME) ? "Dockerfile" : configVars.getValue(DockerTask.DOCKER_FILE_NAME);

        return Paths.get(getAbsoluteWorkingDir() + "/" + dockerfile)
                .toAbsolutePath()
                .normalize()
                .toString();
    }

    public String getUsername(ConfigVars configVars) {
        if (!configVars.isEmpty(DockerTask.USERNAME)) {
            return configVars.getValue(DockerTask.USERNAME);
        }

        return configVars.getValue(DockerTask.REGISTRY_USERNAME);
    }

    public String getDirectory(String dockerFile) {
        return Paths.get(dockerFile).getParent().toString();
    }

    public String getRegistryName(String registryAddress) {
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

    public List<String> getCommand() {
        return command;
    }
}
