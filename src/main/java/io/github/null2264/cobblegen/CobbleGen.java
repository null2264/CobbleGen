package io.github.null2264.cobblegen;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.github.null2264.cobblegen.compat.LoaderCompat;
import io.github.null2264.cobblegen.compat.TextCompat;
import io.github.null2264.cobblegen.data.config.ConfigMetaData;
import io.github.null2264.cobblegen.data.model.CGRegistry;
import io.github.null2264.cobblegen.util.CGLog;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.ApiStatus;

import java.io.File;
import java.nio.file.Path;

import static io.github.null2264.cobblegen.data.config.ConfigHelper.loadConfig;

//#if FORGE>=1
    //#if FORGE==2
    //$$ @net.neoforged.fml.common.Mod(CobbleGen.MOD_ID)
    //#else
    //$$ @net.minecraftforge.fml.common.Mod(CobbleGen.MOD_ID)
    //#endif
//$$ public class CobbleGen
//#else
public class CobbleGen implements net.fabricmc.api.ModInitializer
//#endif
{
    public static final String MOD_ID = "cobblegen";
    /**
     * @deprecated Now only for internal usage. Use the parameter {@link CGRegistry registry} instead
     */
    @Deprecated
    @ApiStatus.Internal
    public static final FluidInteraction FLUID_INTERACTION = new FluidInteraction();
    private static final Path configPath = LoaderCompat.getConfigDir();
    private static final File configFile = new File(configPath + File.separator + MOD_ID + "-meta.json5");
    @ApiStatus.Internal
    public static ConfigMetaData META_CONFIG = loadConfig(false, configFile, null, new ConfigMetaData(), ConfigMetaData.class);

    //#if FABRIC>=1
    @Override
    public void onInitialize() {}
    //#endif

    public static void initCommands(CommandDispatcher<CommandSourceStack> dispatcher) {
        CGLog.info("Registering command...");
        dispatcher.register(
                LiteralArgumentBuilder.<CommandSourceStack>literal("cobblegen")
                        .then(LiteralArgumentBuilder.<CommandSourceStack>literal("reload-meta").executes(c -> {
                            CGLog.info("Reloading meta config...");
                            META_CONFIG = loadConfig(true, configFile, META_CONFIG, new ConfigMetaData(), ConfigMetaData.class);
                            c.getSource().sendSuccess(
                                //#if MC>=1.20.1
                                //$$ () ->
                                //#endif
                                TextCompat.literal("Meta config has been reloaded"), false
                            );
                            CGLog.info("Meta config has been reloaded");
                            return 0;
                        }))
        );
    }

    public enum Channel {
        PING,
        SYNC,
    }
}