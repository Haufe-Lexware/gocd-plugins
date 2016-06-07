package com.tw.go.plugin.common;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class VarsExpander {
    protected Map<String, String> vars;

    public VarsExpander(Map<String, String> vars) {
        this.vars = vars;
    }

    public VarsExpander()
    {
        this(new HashMap<String, String>());
    }

    public VarsExpander add(String key, String val)
    {
        vars.put(key, val);

        return this;
    }

    public String expand(String raw)
    {
        return expand(raw,null);
    }

    public String expand(String raw, Set<String> found)
    {
        String newValue = "";
        int curr = 0;

        for (; ; )
        {
            int next = raw.indexOf("${", curr);

            if (next == -1)
            {
                break;
            }

            newValue = newValue + raw.substring(curr, next);
            next += 2;

            int end = raw.indexOf("}", next);

            if (end == -1)
            {
                break;
            }

            String id = raw.substring(next, end);
            String replace = vars.get(id);
            if (replace != null) {
                newValue += replace;
                if (found != null) {
                    found.add(id);
                }
            }
            curr = end + 1;
        }

        newValue += raw.substring(curr);

        return newValue;
    }

    public Map<String, String> getVars() {
        return vars;
    }
}