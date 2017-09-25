package com.tw.go.task.sonarqualitygate.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum ProjectStatus {
    OK("ok"), WARN("warn"), ERROR("error"), NONE("none");

    @Getter
    public final String value;
}