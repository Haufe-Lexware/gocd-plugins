package com.tw.go.task.dockerpipeline;

import com.thoughtworks.go.plugin.api.task.JobConsoleLogger;
import com.thoughtworks.go.plugin.api.task.TaskExecutionContext;
import com.tw.go.plugin.common.VarsExpander;

import java.util.*;

public class ConfigVars
{
    protected static TaskExecutionContext context;
    protected HashMap<String, String> vars = new HashMap<>();
    protected ArrayList<String> secure;
    protected VarsExpander expander;


    public ConfigVars(Map<String, Map> config)
    {
        this(config, new HashMap<String, String>(), new ArrayList<String>());
    }

    public ConfigVars(Map<String, Map> config, Map<String, String> envVars, ArrayList<String> secure)
    {
        expander = new VarsExpander(envVars);

        for (Map.Entry<String, Map> entry : config.entrySet())
        {
            String key = entry.getKey();
            Map cfg = entry.getValue();

            if ((boolean) cfg.get("secure") && !secure.contains(key))
            {
                secure.add(key);
            }

            vars.put(key, (String)cfg.get("value"));
        }

        Collections.sort(secure, new Comparator<String>()
        {
            @Override
            public int compare(String s, String t1)
            {
                return t1.length() - s.length();
            }
        });

        this.secure = secure;
    }

    public void printConfig(JobConsoleLogger console, String prefix)
    {
        for (Map.Entry<String, String> cfg : vars.entrySet())
        {
            String key = cfg.getKey();

            if (secure.contains(key))
            {
                console.printLine(prefix + key + ": *****");
            }
            else
            {
                console.printLine(prefix + key + ": " + cfg.getValue());
            }
        }
    }

    public String getValue(String key)
    {
        return expander.expand(vars.get(key));
    }

    public String getRawValue(String key)
    {
        return vars.get(key);
    }

    public String mask(String text)
    {
        for (String key : secure)
        {
            String search = getValue(key);
            String replaced = text.replace(search, "*****");
            text = replaced;
        }

        return text;
    }
}