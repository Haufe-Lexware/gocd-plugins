package com.tw.go.task.sonarqualitygate.model;

import lombok.Data;

import java.util.List;

@Data
public class SonarStatus {
    ProjectStatus status;
    List<SonarCondition> conditions;
    List<SonarPeriod> periods;
}