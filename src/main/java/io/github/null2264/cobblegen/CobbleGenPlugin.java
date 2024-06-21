package io.github.null2264.cobblegen;

import io.github.null2264.cobblegen.data.model.CGRegistry;
import org.jetbrains.annotations.ApiStatus;

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

    /**
     * Should this fluid interaction be loaded
     * @return true if it should be loaded
     * @since CobbleGen v5.3.0
     */
    @ApiStatus.AvailableSince("5.3.0")
    default boolean shouldLoad() {
        return true;
    }
}
