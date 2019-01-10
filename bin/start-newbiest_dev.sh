#!/bin/bash
set -x

ROOT="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && cd .. && pwd )"
JAR_PATH=$(find $ROOT -name starter*.jar)

echo "Start JAR from ${JAR_PATH}"
java -jar "${JAR_PATH}" --spring.profiles.active=dev --logging.path=/Users/apple/Documents/newbiest/log
