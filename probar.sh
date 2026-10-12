#!/usr/bin/env sh
set -eu
cd "$(dirname "$0")"
sh build.sh
java -Djava.awt.headless=true -ea -cp build/classes edu.itson.cu6.ModelTests

