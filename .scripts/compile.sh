#!/bin/bash

VERSIONS=($(ls -1 versions | grep -e ".\."))

for i in "${VERSIONS[@]}"; do
	echo "Compiling ${i}"
	./gradlew :${i}:build --stacktrace
done
