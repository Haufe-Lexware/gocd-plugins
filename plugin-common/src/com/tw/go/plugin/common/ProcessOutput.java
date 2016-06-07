package com.tw.go.plugin.common;

import java.util.List;

import static com.tw.go.plugin.common.ListUtil.join;

public class ProcessOutput {
    private int returnCode;
    private List<String> stdOut;
    private List<String> stdErr;

    public ProcessOutput(int returnCode, List<String> stdOut, List<String> stdErr) {
        this.returnCode = returnCode;
        this.stdOut = stdOut;
        this.stdErr = stdErr;
    }

    public int getReturnCode() {
        return returnCode;
    }

    public List<String> getStdOut() {
        return stdOut;
    }

    public List<String> getStdErr() {
        return stdErr;
    }

    public String getStdErrorAsString() {
        if (hasErrors())
            return "Error Message: " + join(getStdErr(), "\n");
        return "";
    }

    public boolean isZeroReturnCode() {
        return returnCode == 0;
    }

    public boolean hasOutput() {
        return stdOut != null && !stdOut.isEmpty();
    }

    public boolean hasErrors() {
        return stdErr != null && !stdErr.isEmpty();
    }

    @Override
    public String toString() {
        return "ProcessOutput{" +
                "returnCode=" + returnCode +
                ", stdOut=" + stdOut +
                ", stdErr=" + stdErr +
                '}';
    }
}