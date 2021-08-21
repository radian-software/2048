#!/usr/bin/env bash

set -euxo pipefail

cd "$(dirname "$0")"

rm -rf bin jar
mkdir -p bin jar

find src -name '*.java' | xargs javac -d bin

cd bin
find . -path '**/console/*.class' | xargs jar cvef com.apprisingsoftware.game2048.console.Applet ../jar/console.jar
find . -path '**/graphical/*.class' | xargs jar cvef com.apprisingsoftware.game2048.graphical.Applet ../jar/graphical.jar
