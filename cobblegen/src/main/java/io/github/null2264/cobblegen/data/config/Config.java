package io.github.null2264.cobblegen.data.config;

import io.github.null2264.cobblegen.compat.LoaderCompat;

import java.nio.file.Path;

public interface Config {
    Path path = LoaderCompat.getConfigDir();
}
