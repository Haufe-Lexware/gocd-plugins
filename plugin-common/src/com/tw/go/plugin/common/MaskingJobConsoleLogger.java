package com.tw.go.plugin.common;

import com.thoughtworks.go.plugin.api.task.JobConsoleLogger;

import java.io.InputStream;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

public class MaskingJobConsoleLogger extends JobConsoleLogger {
    Set<String> maskValues = new HashSet<>();
    String mask = "********";
    String prefix = "";

    public static MaskingJobConsoleLogger getConsoleLogger() {
        if (context == null) throw new RuntimeException("context is null");
        return new MaskingJobConsoleLogger();
    }

    public MaskingJobConsoleLogger withMask(String s) {
        mask = s;
        return this;
    }

    public MaskingJobConsoleLogger withRegexFilter(String s) {
        maskValues.add(s);
        return this;
    }

    public MaskingJobConsoleLogger withPrefix(String prefix) {
        this.prefix = prefix;
        return this;
    }

    public MaskingJobConsoleLogger withTextFilter(String s) {
        maskValues.add("\\b" + Pattern.quote(s) + "\\b");
        return this;
    }

    public MaskingJobConsoleLogger withMapFilter(Map<String, String> map) {
        for (Map.Entry<String, String> e : map.entrySet()) {
            if (context.environment().secureEnvSpecifier().isSecure(e.getKey())) {
                withTextFilter(e.getValue());
            }
        }
        return this;
    }

    public String mask(String line) {
        for (String s : maskValues) {
            line = line.replaceAll(s, mask);
        }
        return line;
    }

    @Override
    public void printLine(String line) {
        super.printLine(prefix + mask(line));
    }

    @Override
    public void printEnvironment(Map<String, String> environment) {
        for (Map.Entry<String, String> e : environment.entrySet()) {
            printLine(e.getKey() + ": " + e.getValue());
        }
    }
}