#!/bin/bash
set -x

ROOT="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && cd .. && pwd )"
WAR_PATH=$(find $ROOT -name framework*.war)

echo "Start WAR from ${WAR_PATH}"
java -jar "${WAR_PATH}"
