package com.tw.go.plugin.common;

import org.apache.commons.io.IOUtils;

import java.util.List;
import java.util.Map;

import static org.apache.commons.io.IOUtils.closeQuietly;

public class ProcessRunner {
    public ProcessOutput execute(List<String> command, Map<String, String> envMap) {
        ProcessBuilder processBuilder = new ProcessBuilder(command);
        Process process = null;
        ProcessOutput processOutput = null;
        try {
            processBuilder.environment().putAll(envMap);
            process = processBuilder.start();
            int returnCode = process.waitFor();
            List outputStream = IOUtils.readLines(process.getInputStream());
            List errorStream = IOUtils.readLines(process.getErrorStream());
            processOutput = new ProcessOutput(returnCode, outputStream, errorStream);
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
        return processOutput;
    }}