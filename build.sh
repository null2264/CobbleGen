#!/bin/sh

help() {
	>&2 echo "./build.sh [mc version] [loader name] [java version]"
	exit 1
}

[ "$1" == "" ] && help
[ "$2" == "" ] && help
[ "$3" == "" ] && help

CONTAINER_MAN="podman"
command -v podman || CONTAINER_MAN="docker"

$CONTAINER_MAN build --tag=cg-build-image -q .
$CONTAINER_MAN run --name=cg-build-1.21.1 --rm -v /${PWD}:/home/build -e MC_VER="$1" -e LOADER_NAME="$2" -e JAVA_VERSION="$3" cg-build-image
