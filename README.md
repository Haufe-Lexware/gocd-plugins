# gocd-plugins

A collection of plugins for use with Go.CD. Have a look at [http://www.go.cd/](http://www.go.cd/) for more information on Go.CD itself.

## SonarQube Quality Gate Plugin
Validate SonarQube quality gates from your go.cd pipeline. Checks if a specific quality gate in SonarQube is passed or if it is in error or warning state. This plugin can prevent further execution of the pipeline if a quality gate is not passed.[SonarQube quality gate Wiki Page](https://github.com/Haufe-Lexware/gocd-plugins/wiki/SonarQube-Quality-Gates-Task-Plugin)

## Nessus Scan Plugin
Execute nessus security scans from go.CD. Configure your pipeline bahaviour based on the result of a scan. More at [Nessus Scan Wiki Page](https://github.com/Haufe-Lexware/gocd-plugins/wiki/Nessus-Scan-Task-Plugin)