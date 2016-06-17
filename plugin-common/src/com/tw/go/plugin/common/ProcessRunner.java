package com.tw.go.plugin.common;

import com.thoughtworks.go.plugin.api.logging.Logger;
import com.thoughtworks.go.plugin.api.task.JobConsoleLogger;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static org.apache.commons.io.IOUtils.closeQuietly;

/**
 * Copied from https://github.com/gocd/go-plugins.git
 */
public class ProcessRunner {
    private static final Logger logger = Logger.getLoggerFor(ProcessRunner.class);

    public ProcessOutput execute(JobConsoleLogger console, boolean showConsoleOutput, String workingDir, List<String> command, Map<String, String> envMap) {
        ProcessBuilder processBuilder = new ProcessBuilder(command);
        Process process = null;
        ProcessOutput processOutput = null;
        long startTime = System.currentTimeMillis();
        try {
            logger.info(String.format("External process '%s' started", command.get(0)));
            if (workingDir != null) {
                processBuilder.directory(new File(workingDir));
            }
            processBuilder.redirectErrorStream(true).environment().putAll(envMap);
            process = processBuilder.start();

            if (showConsoleOutput) {
                LinesInputStream output = new LinesInputStream(process.getInputStream());
                console.readOutputOf(output);
                int returnCode = process.waitFor();
                waitUntilEmpty(2000, output, null);
                processOutput = new ProcessOutput(returnCode, output.getLines(), new ArrayList<String>());
            } else {
                List output = IOUtils.readLines(process.getInputStream());
                int returnCode = process.waitFor();
                List pendingOutput = IOUtils.readLines(process.getInputStream());
                output.addAll(pendingOutput);
                processOutput = new ProcessOutput(returnCode, output, new ArrayList<String>());
            }
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        } finally {
            if (process != null) {
                closeQuietly(process.getInputStream());
                closeQuietly(process.getErrorStream());
                closeQuietly(process.getOutputStream());
                process.destroy();
                long estimatedTime = System.currentTimeMillis() - startTime;
                logger.info(String.format("External process '%s' returned %d after %dms.", command.get(0), processOutput.getReturnCode(), estimatedTime));
            }
        }
        return processOutput;
    }

    public void waitUntilEmpty(int timeout, LinesInputStream output, LinesInputStream error) throws InterruptedException {
        int numOutLines = output.getLines().size();
        int numErrLines = error != null ? error.getLines().size() : 0;
        while (true) {
            logger.info(String.format("Waiting %dms for stdour/stderr to settle ...", timeout));
            Thread.sleep(timeout, 0);
            int newNumOutLines = output.getLines().size();
            int newNumErrLines = error != null ? error.getLines().size() : 0;
            if (numOutLines == newNumOutLines && newNumErrLines == numErrLines) {
                logger.info("stdour/stderr no longer changing");
                break;
            }
            numOutLines = newNumOutLines;
            numErrLines = newNumErrLines;
        }
    }
}
