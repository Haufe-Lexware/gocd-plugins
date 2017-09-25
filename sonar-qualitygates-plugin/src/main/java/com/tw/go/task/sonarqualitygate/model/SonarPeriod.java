package com.tw.go.task.sonarqualitygate.model;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class SonarPeriod {
    Integer index;
    String mode;
    String date;
}