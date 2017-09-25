package com.tw.go.task.sonarqualitygate.model;

import lombok.Data;

@Data
public class SonarCondition {
    String status;
    String metricKey;
    String comparator;

    Integer periodIndex;
    String errorThreshold;
    String actualValue;
}