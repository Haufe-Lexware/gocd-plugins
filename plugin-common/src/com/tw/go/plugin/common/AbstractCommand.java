package com.tw.go.plugin.common;

import com.thoughtworks.go.plugin.api.task.JobConsoleLogger;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class AbstractCommand implements ICommand {
    protected List<String> command = new ArrayList<>();
    protected Map<String, String> environment = new HashMap<>();
    protected JobConsoleLogger console;
    private ProcessRunner processRunner;
    protected boolean ignoreErrors = false;
    protected boolean showConsoleOutput = true;
    private ProcessOutput processOutput = null;

    protected static String workingDir = null;

    public static String getAbsoluteWorkingDir() {
        return workingDir;
    }

    public static void setWorkingDir(String workingDir) {
        AbstractCommand.workingDir = Paths.get(workingDir).toAbsolutePath().normalize().toString();
    }

    public AbstractCommand(JobConsoleLogger console) {
        this(console, new ProcessRunner(), false);
    }

    public AbstractCommand(JobConsoleLogger console, boolean ignoreErrors) {
        this(console, new ProcessRunner(), ignoreErrors);
    }

    public AbstractCommand(JobConsoleLogger console, ProcessRunner processRunner, boolean ignoreErrors) {
        this.processRunner = processRunner;
        this.console = console;
        this.ignoreErrors = ignoreErrors;
    }

    static public Map splitKeyValues(String s) {
        Map m = new HashMap<>();
        String k = "";
        String t = "";
        char c;
        s += ';';
        for (int i = 0; i < s.length(); ) {
            c = s.charAt(i++);
            switch (c) {
                // unescaped , or ; is separator between key/value pairs
                case ';':
                case ',':
                    if (!k.isEmpty()) {
                        m.put(k, t);
                        k = t = "";
                    }
                    break;
                // first '=' is separator between key and value. subsequent '=' are treated as part of value
                case '=':
                    if (k.isEmpty()) {
                        k = t.trim();
                        t = "";
                    } else {
                        t += c;
                    }
                    break;
                // Any character after a backslash ('\') is simply appended to t.
                case '\\':
                    c = s.charAt(i++);
                default:
                    t += c;
                    break;
            }
        }
        return m;
    }

    public AbstractCommand disableConsoleOutput() {
        this.showConsoleOutput = false;
        return this;
    }

    @Override
    public void run() throws Exception {
        if (showConsoleOutput) {
            console.printLine("Run " + renderDisplay());
        }

        synchronized (AbstractCommand.class) {
            processOutput = processRunner.execute(console, showConsoleOutput, workingDir, command, environment);
        }

        if (!isSuccessful()) {
            if (!ignoreErrors) {
                throw new RuntimeException("External process failed. See Job console output for more information.");
            }
        }
    }

    public ProcessOutput getProcessOutput() {
        return processOutput;
    }

    public boolean isSuccessful() {
        return processOutput != null && processOutput.isZeroReturnCode();
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

    protected AbstractCommand addEnvFromConfig(ConfigVars configVars, String key) {
        addEnv(key, configVars.getValue(key));
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