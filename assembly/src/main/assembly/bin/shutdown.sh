#!/bin/bash
#
# Copyright 2013 James A. Shepherd
# http://www.JamesAshepherd.com/
# LICENCE: http://www.gnu.org/licenses/lgpl.html

cd $(dirname $0)

java -Dstart.port=6666 -Dstart.code=6666 -jar ../lib/sshproxyj-start-${project.version}.jar --shutdown