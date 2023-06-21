package io.github.null2264.cobblegen;

import io.github.null2264.cobblegen.compat.LoaderCompat;
import io.github.null2264.cobblegen.config.ConfigMetaData;
import io.github.null2264.cobblegen.data.FluidInteractionHelper;
import io.github.null2264.cobblegen.data.model.CGRegistry;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.nio.file.Path;

import static io.github.null2264.cobblegen.config.ConfigHelper.loadConfig;

//#if FABRIC<=0
//$$ @net.minecraftforge.fml.common.Mod(CobbleGen.MOD_ID)
//$$ public class CobbleGen
//#else
public class CobbleGen implements net.fabricmc.api.ModInitializer
//#endif
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
    private static final Path configPath = LoaderCompat.getConfigDir();
    private static final File configFile = new File(configPath + File.separator + MOD_ID + "-meta.json5");
    @ApiStatus.Internal
    @Nullable
    public static ConfigMetaData META_CONFIG = loadConfig(false, configFile, null, new ConfigMetaData(), ConfigMetaData.class);

    //#if FABRIC>=1
    @Override
    public void onInitialize() {}
    //#endif

    public enum Channel {
        PING,
        SYNC,
    }
}
