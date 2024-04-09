#!/bin/bash

# REF: https://github.com/embeddedt/embeddium/blob/310ff4ca5d5a0ba49c72f2011d51f35d7308f57f/scripts/compile_kits.sh

set -e

MC_VERSION=$(cat snapshot_version 2>/dev/null)
SHOULD_COMPILE="y"

if [[ $MC_VERSION == *"w"** ]]; then
    branch=port/${MC_VERSION}
    echo "Detected MC snapshot ${MC_VERSION}"
    echo "Downloading Kits ${branch}..."
    cd ~
    git clone -q -b ${branch} --depth 1 https://github.com/neoforged/NeoForge Kits >/dev/null || (SHOULD_COMPILE="n"; echo "Already exists, skipping...")
    cd Kits
    [ $SHOULD_COMPILE = "y" ] && echo "Compiling Kits" && ./gradlew neoforge:setup
    ./gradlew neoforge:publishToMavenLocal
fi
