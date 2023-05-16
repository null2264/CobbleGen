package io.github.null2264.cobblegen;

import io.github.null2264.cobblegen.data.model.CGRegistry;

public interface CobbleGenPlugin
{
    void registerInteraction(CGRegistry registry);

    default void onReload() {
    }
}