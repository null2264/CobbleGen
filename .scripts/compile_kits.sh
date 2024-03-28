#!/bin/bash

# REF: https://github.com/embeddedt/embeddium/blob/310ff4ca5d5a0ba49c72f2011d51f35d7308f57f/scripts/compile_kits.sh

set -e

function prop {
    grep "${1}" gradle.properties | cut -d'=' -f2
}

MC_VERSION="24w12a"

if [[ $MC_VERSION == *"w"** ]]; then
    branch=port/${MC_VERSION}
    echo "Detected MC snapshot ${MC_VERSION}"
    cd $(mktemp -d)
    echo "Downloading Kits ${branch}..."
    git clone -q -b ${branch} --depth 1 https://github.com/neoforged/NeoForge Kits >/dev/null
    cd Kits
    echo "Compiling Kits"
    ./gradlew neoforge:setup
    ./gradlew neoforge:publishToMavenLocal
fi
