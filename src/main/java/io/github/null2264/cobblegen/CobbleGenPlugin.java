package io.github.null2264.cobblegen;

import io.github.null2264.cobblegen.data.model.CGRegistry;

public interface CobbleGenPlugin
{
    /**
     * Register new Fluid Interaction
     */
    void registerInteraction(CGRegistry registry);

    /**
     * Function that will be run when config is being reloaded
     */
    default void onReload() {
    }
}