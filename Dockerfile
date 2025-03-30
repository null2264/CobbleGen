FROM ubuntu:24.04

RUN rm /bin/sh && ln -s /bin/bash /bin/sh
RUN apt-get update && apt-get -y install curl unzip zip

RUN curl -s "https://get.sdkman.io" | bash

RUN source "$HOME/.sdkman/bin/sdkman-init.sh" \
	&& sdk install java ${JAVA_VERSION:-"21.0.6"}-tem \
	&& sdk default java ${JAVA_VERSION:-"21.0.6"}-tem

ENV JAVA_HOME /root/.sdkman/candidates/java/current

WORKDIR /home/build/
COPY ./gradlew .
RUN chmod +x ./gradlew
CMD echo "\r========== [CLEAN: $MC_VER-$LOADER_NAME] ==========" && \
    ./gradlew clean -PmcVer="$MC_VER" -PloaderName="$LOADER_NAME" --gradle-user-home .gradle-cache/ && \
    echo "\r========== [BUILD: $MC_VER-$LOADER_NAME] ==========" && \
    ./gradlew build -PmcVer="$MC_VER" -PloaderName="$LOADER_NAME" --gradle-user-home .gradle-cache/ && \
    echo "\r========== [DONE:  $MC_VER-$LOADER_NAME] =========="
