# gocd-plugins

> :warning: **This project is no longer maintained**

A collection of plugins for use with Go.CD. Have a look at [http://www.go.cd/](http://www.go.cd/) for more information on Go.CD itself.

## SonarQube Quality Gate Plugin
Validate SonarQube quality gates from your go.cd pipeline. Checks if a specific quality gate in SonarQube is passed or if it is in error or warning state. This plugin can prevent further execution of the pipeline if a quality gate is not passed.[SonarQube quality gate Wiki Page](https://github.com/Haufe-Lexware/gocd-plugins/wiki/SonarQube-Quality-Gates-Task-Plugin)

## Nessus Scan Plugin
Execute nessus security scans from go.CD. Configure your pipeline bahaviour based on the result of a scan. More at [Nessus Scan Wiki Page](https://github.com/Haufe-Lexware/gocd-plugins/wiki/Nessus-Scan-Task-Plugin)

## Check_MK monitoring plugin
Manage a host on a Check_MK monitoring server from a go.cd pipeline. Add or remove hosts directly to Check_MK using pipelines. More at [Check_MK Monitoring Wiki Page](https://github.com/Haufe-Lexware/gocd-plugins/wiki/Check_Mk-Monitoring-Task-Plugin)

## Docker pipeline plugin
Builds a docker image from you material, tags it with the tag you set, pushes it to a registry you specify and then cleans up everything. More at [Docker pipeline plugin](https://github.com/Haufe-Lexware/gocd-plugins/wiki/Docker-pipeline-plugin)
