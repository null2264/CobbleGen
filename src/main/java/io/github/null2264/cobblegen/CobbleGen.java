package io.github.null2264.cobblegen;

import io.github.null2264.cobblegen.data.FluidInteractionHelper;
import io.github.null2264.cobblegen.data.model.CGRegistry;
import net.fabricmc.api.ModInitializer;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.ApiStatus;

public class CobbleGen implements ModInitializer
{
    public static final String MOD_ID = "cobblegen";
    public static final ResourceLocation SYNC_CHANNEL = new ResourceLocation(MOD_ID, "sync");
    public static final ResourceLocation SYNC_PING_CHANNEL = new ResourceLocation(MOD_ID, "sync_ping");
    /**
     * @deprecated Now only for internal usage. Use the parameter {@link CGRegistry registry} instead
     */
    @Deprecated
    @ApiStatus.Internal
    public static final FluidInteractionHelper FLUID_INTERACTION = new FluidInteractionHelper();

    @Override
    public void onInitialize() {
        //FLUID_INTERACTION.apply();
    }

    public enum Channel {
        PING,
        SYNC,
    }
}