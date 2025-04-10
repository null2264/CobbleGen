package io.github.null2264.cobblegen.data.config;

import java.io.File;

public interface Config {
    interface Factory<T extends Config> {
        T load(File file);
        T reload(File file);
    }
}
