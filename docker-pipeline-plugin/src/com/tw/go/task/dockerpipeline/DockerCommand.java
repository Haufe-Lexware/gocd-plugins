package com.tw.go.task.dockerpipeline;

import com.thoughtworks.go.plugin.api.task.JobConsoleLogger;
import com.tw.go.plugin.common.AbstractCommand;
import com.tw.go.plugin.common.ConfigVars;

import java.util.ArrayList;

/**
 * Created by thomassc on 30.06.16.
 */
public class DockerCommand extends AbstractCommand {

    public DockerCommand(JobConsoleLogger console, ConfigVars configVars) {
        super(console);

        // 1. just copy from the current environment
        addEnv(configVars.environmentVars());
    }

    protected ArrayList<String> splitArgs(String argString) {
        ArrayList<String> args = new ArrayList<String>();

        int idx = 0;
        argString += " ";
        String token = "";
        boolean inQuotes = false;
        char c;
        while (true) {
            if (idx == argString.length()) {
                break;
            }
            switch (c = argString.charAt(idx++)) {
                case '"':
                    if (inQuotes) {
                        if (token.length() > 0) {
                            args.add(token);
                            token = "";
                        }
                        inQuotes = false;
                    } else {
                        inQuotes = true;
                    }
                    break;
                case ' ':
                    if (inQuotes) {
                        token += c;
                    } else {
                        if (token.length() > 0) {
                            args.add(token);
                            token = "";
                        }
                    }
                    break;
                case '\\':
                    c = argString.charAt(idx++);
                default:
                    token += c;
                    break;
            }
        }

        return args;
    }
}