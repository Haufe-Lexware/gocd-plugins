#!/bin/bash

mvn install:install-file \
-Dfile=bin/go-plugin-api-15.1.0.jar \
-DgroupId=com.thoughtworks.go \
-DartifactId=go-plugin-api \
-Dversion=15.1.0 \
-Dpackaging=jar && \
\
mvn clean \
package \
-DskipTests && \
\
mvn cobertura:cobertura
