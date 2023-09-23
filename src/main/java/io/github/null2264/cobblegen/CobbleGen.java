package io.github.null2264.cobblegen;

import io.github.null2264.cobblegen.data.FluidInteractionHelper;
import io.github.null2264.cobblegen.data.model.CGRegistry;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.ApiStatus;

//#if FABRIC<=0
//$$ @net.minecraftforge.fml.common.Mod(CobbleGen.MOD_ID)
//$$ public class CobbleGen
//#else
public class CobbleGen implements net.fabricmc.api.ModInitializer
//#endif
{
    public static final String MOD_ID = "cobblegen";
    public static final ResourceLocation SYNC_CHANNEL = new ResourceLocation(MOD_ID, "sync");
    public static final ResourceLocation SYNC_PING_CHANNEL = new ResourceLocation(MOD_ID, "ping");
    /**
     * @deprecated Now only for internal usage. Use the parameter {@link CGRegistry registry} instead
     */
    @Deprecated
    @ApiStatus.Internal
    public static final FluidInteractionHelper FLUID_INTERACTION = new FluidInteractionHelper();

    //#if FABRIC>=1
    @Override
    public void onInitialize() {}
    //#endif

    public enum Channel {
        PING,
        SYNC,
    }
}