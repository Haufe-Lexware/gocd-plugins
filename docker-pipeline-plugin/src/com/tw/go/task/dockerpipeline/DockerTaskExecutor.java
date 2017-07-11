package com.tw.go.task.dockerpipeline;

import com.thoughtworks.go.plugin.api.logging.Logger;
import com.thoughtworks.go.plugin.api.task.JobConsoleLogger;
import com.tw.go.plugin.common.*;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.util.Map;

public class DockerTaskExecutor extends TaskExecutor {
    private final Logger logger = Logger.getLoggerFor(DockerTaskExecutor.class);

    public DockerTaskExecutor(JobConsoleLogger console, Context context, Map config) {
        super(console, context, config);
    }

    public Result execute() throws Exception {
        try {
            return runCommand();
        } catch (Exception e) {
            logException(logger,e);
            return new Result(false, getPluginLogPrefix() + e.getMessage(), e);
        }
    }

    public Result runCommand() throws Exception {
        try {
            if(configVars.isChecked(DockerTask.CLEAN_BEFORE_TASK)) {
                new DockerRestartCommand(console, configVars)
                        .run();
                new DockerStopContainers(console, configVars)
                        .run();
                new DockerRemoveAllContainers(console, configVars)
                        .run();
                new DockerRemoveAllImages(console, configVars)
                        .run();
            }
            if (!configVars.isEmpty(DockerTask.REGISTRY_USERNAME) && !configVars.isEmpty(DockerTask.REGISTRY_PASSWORD)) {
                new DockerLoginCommand(console, configVars)
                        .run();
            }

            if (!configVars.isEmpty(DockerTask.IMAGE_TAG)) {
                DockerBuildCommand build;

                if (configVars.isChecked(DockerTask.BUILD_MICROLABELING))
                    build = new DockerBuildCommand(console, configVars, getRepositoryUrl());
                else
                    build = new DockerBuildCommand(console, configVars, "");

                build.run();

                if (configVars.isChecked(DockerTask.PUSH_IMAGES)) {
                    for (String tag : build.imageAndTag) {
                        if (tag != null) {
                            new DockerPushCommand(console, configVars, tag)
                                    .run();
                        }
                    }
                }
            }

            if (!configVars.isEmpty(DockerTask.RUN_IMAGE)) {
                new DockerRun2Command(console, configVars)
                        .run();
            }
        } finally {
            if (configVars.isChecked(DockerTask.CLEAN_AFTER_TASK)) {
                new DockerStopContainers(console, configVars)
                        .run();
                new DockerRemoveAllContainers(console, configVars)
                        .run();
                new DockerRemoveAllImages(console, configVars)
                        .run();
            }

            if (configVars.isChecked(DockerTask.CLEAN_AFTER_TASK)) {
                // http://blog.yohanliyanage.com/2015/05/docker-clean-up-after-yourself/
                new DockerRemoveExitedContainers(console, configVars)
                        .run();
                new DockerRemoveDanglingImages(console, configVars)
                        .run();
            }
        }

        return new Result(true, "Finished");
    }

    private String getRepositoryUrl() throws GeneralSecurityException {
        Map envVars = context.getEnvironmentVariables();
        GoApiClient client = new GoApiClient(envVars.get(GoApiConstants.ENVVAR_NAME_GO_SERVER_URL).toString());
        try {
            // get go build user authorization
            if (envVars.get(GoApiConstants.ENVVAR_NAME_GO_BUILD_USER) != null &&
                    envVars.get(GoApiConstants.ENVVAR_NAME_GO_BUILD_USER_PASSWORD) != null) {

                client.setBasicAuthentication(envVars.get(GoApiConstants.ENVVAR_NAME_GO_BUILD_USER).toString(), envVars.get(GoApiConstants.ENVVAR_NAME_GO_BUILD_USER_PASSWORD).toString());

                log("Logged in as '" + envVars.get(GoApiConstants.ENVVAR_NAME_GO_BUILD_USER).toString() + "'");

                Map response = client.getPipelineConfig(envVars.get
                                (GoApiConstants.ENVVAR_NAME_GO_PIPELINE_NAME).toString(),
                                envVars.get(GoApiConstants.ENVVAR_NAME_GO_PIPELINE_COUNTER).toString());

                //log("respose" + response);

                JSONObject objResult = new JSONObject(response);
                //log("objResult" + objResult);
                JSONObject build_cause = objResult.getJSONObject("build_cause");
                //log("build_case" + build_cause);
                JSONArray material_revisions = build_cause.getJSONArray("material_revisions");
                //log("material_revisions" + material_revisions);
                JSONObject materialFirst = material_revisions.getJSONObject(0);
                //log("materialfirst" + materialFirst);
                JSONObject material = materialFirst.getJSONObject("material");
                //log("material" + material);
                String url = material.getString("description");
                //log("url" + url);

                if (url.contains("@")) {
                    String result = "http://" + url.substring(url.indexOf("@") + 1, url.indexOf(','));
                    return result;
                }
                else {
                    String result = "http://" + url.substring(url.indexOf("//") + 1, url.indexOf(','));
                    return result;
                }

            } else {
                log("No login set. Cannot access go.cd API !");
            }
        }
        catch(Exception e) {
            log(e.toString());
        }
        return null;
    }

    @Override
    protected String getPluginLogPrefix() {
        return "[Docker] ";
    }
}
