#!/bin/bash

cd $(dirname $0)

java -Dstart.port=6666 -Dstart.code=6666 -jar ../lib/sshproxyj-start-${project.version}.jar --startup