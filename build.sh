#!/usr/bin/env sh
set -eu
cd "$(dirname "$0")"
mkdir -p build/classes dist
javac --release 17 -encoding UTF-8 -d build/classes ./*.java
jar --create --file dist/cu6-comprar-producto.jar --main-class edu.itson.cu6.Main -C build/classes .

