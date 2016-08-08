package com.tw.go.task.fetchanyartifact;

import com.thoughtworks.go.plugin.api.task.JobConsoleLogger;
import com.thoughtworks.go.plugin.api.logging.Logger;
import com.tw.go.plugin.common.*;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Map;

import static com.tw.go.plugin.common.GoApiConstants.*;
import static com.tw.go.task.fetchanyartifact.FetchAnyArtifactTask.*;

/**
 * Created by thomassc on 19.05.16.
 */
public class FetchAnyArtifactTaskExecutor extends TaskExecutor {

    private final Logger logger = Logger.getLoggerFor(FetchAnyArtifactTaskExecutor.class);

    public FetchAnyArtifactTaskExecutor(JobConsoleLogger console, Context context, Map config) {
        super(console, context, config);
    }

    @Override
    public Result execute() throws Exception {
        try {
            return runCommand();
        } catch (Exception e) {
            logException(logger,e);
            return new Result(false, getPluginLogPrefix() + e.getMessage(), e);
        }
    }

    public Result runCommand() throws Exception {
        if (configVars.isEmpty(ENVVAR_NAME_GO_BUILD_USER) ||
                configVars.isEmpty(ENVVAR_NAME_GO_BUILD_USER_PASSWORD)) {
            throw new Exception("You must set environment variables '"
                    + ENVVAR_NAME_GO_BUILD_USER
                    + "' and '"
                    + ENVVAR_NAME_GO_BUILD_USER_PASSWORD + "'");
        }

        GoApiClient apiClient = new GoApiClient(
                configVars.getValue(ENVVAR_NAME_GO_SERVER_URL));

        apiClient.setBasicAuthentication(
                configVars.getValue(ENVVAR_NAME_GO_BUILD_USER),
                configVars.getValue(ENVVAR_NAME_GO_BUILD_USER_PASSWORD));

        String artifact = configVars.getValue(FAA_ARTIFACT_SOURCE);
        if (!configVars.isChecked(FAA_ARTIFACT_IS_FILE)) {
            artifact += ".zip";
        }

        Path absoluteWokingDir = Paths.get(AbstractCommand.getAbsoluteWorkingDir()).normalize().toAbsolutePath();
        Path targetDir = Paths.get(absoluteWokingDir.toString(), configVars.getValue(FAA_ARTIFACT_DESTINATION)).normalize().toAbsolutePath();

        if (!targetDir.startsWith(absoluteWokingDir)) {
            throw new Exception("Trying to escape working directory");
        }

        if (!extractArtifactFromPreviousRun(apiClient, artifact, targetDir.toString())) {
            console.printLine("No successful pipeline run found (with a valid artifact to retrieve)");
        } else {
            console.printLine("Successful pipeline run found (with a valid artifact to retrieve)");
        }
            return new Result(true, "Finished");
    }

    public boolean extractArtifactFromPreviousRun(GoApiClient apiClient, String artifact, String targetDirectory) throws IOException {
        String pipelineName = configVars.getValue(FAA_PIPELINE_NAME);
        if (pipelineName.isEmpty()) {
            pipelineName = configVars.getValue(ENVVAR_NAME_GO_PIPELINE_NAME);
        }
        String pipelineStage = configVars.getValue(FAA_STAGE_NAME);
        if (pipelineStage.isEmpty()) {
            pipelineStage = configVars.getValue(ENVVAR_NAME_GO_STAGE_NAME);
        }
        String pipelineJob = configVars.getValue(FAA_JOB_NAME);
        if (pipelineJob.isEmpty()) {
            pipelineJob = configVars.getValue(ENVVAR_NAME_GO_JOB_NAME);
        }

        console.printLine(String.format("Searching for pipeline run '%s/%s/%s/%s'",pipelineName,pipelineStage,pipelineJob,artifact));

        for (int offset = 0; ; ) {
            Map history = apiClient.getPipelineHistory(pipelineName, offset);
            ArrayList pipelines = Selector.select(history, "pipelines");
            int size = pipelines.size();
            if (size == 0) {
                return false;
            }
            for (int pidx = 0; pidx < size; pidx++) {
                Map pipeline = (Map) pipelines.get(pidx);
                Integer pipelineCounter = Selector.select(pipeline, "counter", 0);
                ArrayList stages = Selector.select(pipeline, "stages");

                for (int sidx = 0; sidx < stages.size(); sidx++) {
                    Map stage = (Map) stages.get(sidx);
                    String stageName = Selector.select(stage, "name");
                    Integer stageCounter = Integer.parseInt(Selector.select(stage, "counter", "0"));

                    if ("passed".equalsIgnoreCase(Selector.select(stage, "result", "failed")) &&
                            configVars.isChecked(FAA_FETCH_IF_FAILED)) {
                        ArrayList jobs = Selector.select(stage, "jobs");

                        for (int jidx = 0; jidx < jobs.size(); jidx++) {
                            Map job = (Map) jobs.get(jidx);
                            String jobName = Selector.select(job, "name");

                            if (pipelineStage.equalsIgnoreCase(stageName) &&
                                    pipelineJob.equalsIgnoreCase(jobName)) {

                                try {
                                    InputStream input = apiClient.getArtifact(
                                            pipelineName,
                                            pipelineCounter.toString(),
                                            stageName,
                                            stageCounter.toString(),
                                            jobName,
                                            artifact);

                                    UnzipUtil.unzip(input, targetDirectory);

                                    return true;

                                } catch (IOException e) {
                                    // There is no artifact to be found
                                    return false;
                                }
                            }
                        }
                    }
                }
            }

            offset += size;
        }
    }

    @Override
    protected String getPluginLogPrefix() {
        return "[FetchAnyArtifact] ";
    }
}