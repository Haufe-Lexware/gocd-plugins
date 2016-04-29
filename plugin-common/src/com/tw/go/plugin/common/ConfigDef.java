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
        return add(id, defaultValue, Required.Yes, Secure.No);
    }

    public ConfigDef add(String id, String defaultValue, boolean required)
    {
        return add(id, defaultValue, required, Secure.No);
    }

    public ConfigDef add(String id, String defaultValue, boolean required, boolean secure)
    {
        HashMap<String, Object> entry = new HashMap<>();

        entry.put("default-value", defaultValue);
        entry.put("secure", secure);
        entry.put("required", required);
        map.put(id, entry);

        return this;
    }

    public Map toMap()
    {
        return map;
    }

    public static class Required
    {
        public static boolean Yes = true;
        public static boolean No = false;
    }

    public static class Secure
    {
        public static boolean Yes = true;
        public static boolean No = false;
    }
}