#!/bin/bash
#
# Import git.yizhibo.tv cert for gradle
# Pleasy use "sudo -E" when you use root to run

current_path=`pwd`
cert_file=`pwd`/mvn.cer

if [ -z "$JAVA_HOME" ]; then
    echo "Please define you JAVA_HOME first!"
    exit 1
fi
cd $JAVA_HOME/jre/lib/security
keytool -importcert -alias yizhibo -file "$cert_file" -keystore cacerts -storepass changeit

cd $current_path
