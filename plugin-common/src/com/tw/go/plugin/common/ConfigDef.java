package com.tw.go.plugin.common;

import java.util.HashMap;
import java.util.Map;

public class ConfigDef {

    private Map map;
    private Map config;
    private Map vars;

    public ConfigDef()
    {
        this(new HashMap());
    }

    public ConfigDef(Map map)
    {
        withMap(map);
    }

    public ConfigDef withMap(Map map)
    {
        this.map = map;
        return this;
    }

    public ConfigDef add(String id, String defaultValue)
    {
        return add(id, defaultValue, Required.YES, Secure.NO);
    }

    public ConfigDef add(String id, String defaultValue, Required required)
    {
        return add(id, defaultValue, required, Secure.NO);
    }

    public ConfigDef add(String id, String defaultValue, Required required, Secure secure)
    {
        HashMap<String, Object> entry = new HashMap<>();

        entry.put("default-value", defaultValue);
        entry.put("secure", Secure.YES == secure);
        entry.put("required", Required.YES == required);
        map.put(id, entry);

        return this;
    }

    public Map toMap()
    {
        return map;
    }
}


