#!/bin/bash
set -x

ROOT="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && cd .. && pwd )"
JAR_PATH=$(find $ROOT -name starter*.jar)

echo "Start JAR from ${JAR_PATH}"
java -jar -XX:MetaspaceSize=128m -XX:MaxMetaspaceSize=256m -Xms2048m -Xmx3072m -XX:SurvivorRatio=8 "${JAR_PATH}" --spring.profiles.active=production --logging.path=/Users/apple/Documents/newbiest/log
