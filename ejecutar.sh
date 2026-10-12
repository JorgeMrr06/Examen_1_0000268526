#!/usr/bin/env sh
set -eu
cd "$(dirname "$0")"
if [ ! -f dist/cu6-comprar-producto.jar ]; then sh build.sh; fi
java -jar dist/cu6-comprar-producto.jar "$@"

