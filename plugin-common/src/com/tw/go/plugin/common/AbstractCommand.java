package com.tw.go.plugin.common;

import com.thoughtworks.go.plugin.api.task.JobConsoleLogger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class AbstractCommand implements ICommand {
    protected List<String> command = new ArrayList<>();
    protected Map<String, String> environment = new HashMap<>();
    protected JobConsoleLogger logger;
    private ProcessRunner processRunner;
    private boolean ignoreErrors = false;
    private boolean showConsoleOutput = true;
    private ProcessOutput processOutput = null;

    public AbstractCommand(JobConsoleLogger logger) {
        this(logger, new ProcessRunner(), false);
    }

    public AbstractCommand(JobConsoleLogger logger, boolean ignoreErrors) {
        this(logger, new ProcessRunner(), ignoreErrors);
    }

    public AbstractCommand(JobConsoleLogger logger, ProcessRunner processRunner, boolean ignoreErrors) {
        this.processRunner = processRunner;
        this.logger = logger;
        this.ignoreErrors = ignoreErrors;
    }

    public AbstractCommand disableConsoleOutput() {
        this.showConsoleOutput = false;
        return this;
    }

    @Override
    public void run() throws Exception {
        if (showConsoleOutput) {
            logger.printLine("Run " + renderDisplay());
        }

        synchronized (AbstractCommand.class) {
            processOutput = processRunner.execute(command, environment);
        }

        if (showConsoleOutput) {
            for (String s : processOutput.getStdOut()) {
                logger.printLine(s);
            }
        }

        if (!isSuccessful(processOutput)) {
            if (ignoreErrors) {
                if (showConsoleOutput) {
                    for (String s : processOutput.getStdErr()) {
                        logger.printLine(s);
                    }
                }
            } else {
                String message = processOutput.getStdErrorAsString();
                throw new RuntimeException(message);
            }
        }
    }

    public ProcessOutput getProcessOutput() {
        return processOutput;
    }

    private boolean isSuccessful(ProcessOutput processOutput) {
        return processOutput != null && processOutput.isZeroReturnCode() && processOutput.hasOutput() && !processOutput.hasErrors();
    }

    public AbstractCommand add(String setting) {
        command.add(setting);
        return this;
    }

    public AbstractCommand addEnv(String name, String value) {
        environment.put(name, value);
        return this;
    }

    public AbstractCommand addEnv(Map map) {
        environment.putAll(map);
        return this;
    }

    protected String renderDisplay() {
        StringBuilder sb = new StringBuilder();

        for (String s : command) {
            if (sb.length() > 0) {
                sb.append(' ');
            }

            if (sb.length() == 0 || s.charAt(0) == '-') {
                sb.append(s);
            } else {
                sb.append('"');
                sb.append(s);
                sb.append('"');
            }
        }

        return sb.toString();
    }
}