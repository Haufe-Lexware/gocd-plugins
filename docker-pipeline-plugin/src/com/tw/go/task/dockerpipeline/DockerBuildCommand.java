package com.tw.go.task.dockerpipeline;

import com.thoughtworks.go.plugin.api.task.JobConsoleLogger;
import com.tw.go.plugin.common.*;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static org.apache.commons.io.IOUtils.closeQuietly;

public class DockerBuildCommand extends AbstractCommand {
    protected List<String> imageAndTag = new ArrayList<>();

    public DockerBuildCommand(JobConsoleLogger console, ConfigVars configVars) {
        super(console);
        String dockerfileAbsolutePath = getDockerfileAbsolutePath(configVars); // the path including the Dockerfile. Used for pointing to the Dockerfile
        String context = getDirectory(dockerfileAbsolutePath); // the path without the Dockerfile. Used for obtaining the build context

        String username = getUsername(configVars);
        String registryName = getRegistryName(configVars.getValue(DockerTask.REGISTRY_URL));
        String baseImageName = makeBaseName(registryName, username, configVars.getValue(DockerTask.IMAGE_NAME));

        add("docker");
        add("build");

//        add("--pull=true");
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
        String taggedName;

        for (String tag : ListUtil.splitByFirstOrDefault(tags, ';')) {
            if (!tag.isEmpty()) {
                if (!tagPostfix.isEmpty()) {
                    tag += "-" + tagPostfix;
                }

                if(!("".equals(baseName))) {
                    taggedName = baseName + ":" + tag;

                    command.add("-t");
                    command.add(taggedName);
                    imageAndTag.add(taggedName);
                }
                else {
                    taggedName = tag;

                    command.add("--tag=" + taggedName);
                    imageAndTag.add(taggedName);
                }
            }
        }
    }

    private String makeBaseName(String registry, String username, String imageName) {
        if ("".equals(registry)) {
            if ("".equals(username)) {
                if ("".equals(imageName)) {
                    return "";
                }
                return imageName;
            } else {
                if ("".equals(imageName)) {
                    return username;
                }
                return username + "/" + imageName;
            }
        } else {
            if ("".equals(username)) {
                if ("".equals(imageName)) {
                    return registry;
                }
                return registry + "/" + imageName;
            }
        }

        if ("".equals(imageName)) {
            return registry + "/" + username;
        }

        return registry + "/" + username + "/" + imageName;
    }

    private String getDockerfileAbsolutePath(ConfigVars configVars) {
        String dockerfile = configVars.isEmpty(DockerTask.DOCKER_FILE_NAME) ?
                "Dockerfile" : configVars.getValue(DockerTask.DOCKER_FILE_NAME);

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

    @Override
    public void run() throws Exception {
        console.printLine("Run " + renderDisplay());

        synchronized (AbstractCommand.class) {
            ProcessBuilder processBuilder = new ProcessBuilder(command);
            Process process = null;
            try {
                if (workingDir != null) {
                    processBuilder.directory(new File(workingDir));
                }
                processBuilder.environment().putAll(environment);
                process = processBuilder.start();
                MaskingInputStream misOutput = new MaskingInputStream(process.getInputStream(),(MaskingJobConsoleLogger) console);
                MaskingInputStream misError = new MaskingInputStream(process.getErrorStream(),(MaskingJobConsoleLogger) console);
                console.readOutputOf(misOutput);
                console.readErrorOf(misError);
                int returnCode = process.waitFor();
                if (returnCode != 0) {
                    throw new Exception(String.format("Process returned %d", returnCode));
                }
            } catch (Exception e) {
                throw new RuntimeException(e.getMessage());
            } finally {
                if (process != null) {
                    closeQuietly(process.getInputStream());
                    closeQuietly(process.getErrorStream());
                    closeQuietly(process.getOutputStream());
                    process.destroy();
                }
            }
        }
    }

}