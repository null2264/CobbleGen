package io.github.null2264.cobblegen;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.github.null2264.cobblegen.compat.LoaderCompat;
import io.github.null2264.cobblegen.compat.TextCompat;
import io.github.null2264.cobblegen.data.config.ConfigData;
import io.github.null2264.cobblegen.data.config.ConfigMetaData;
import io.github.null2264.cobblegen.data.model.CGRegistry;
import io.github.null2264.cobblegen.util.CGLog;
import net.minecraft.commands.CommandSourceStack;
import org.jetbrains.annotations.ApiStatus;

import java.io.File;
import java.nio.file.Path;

import static io.github.null2264.cobblegen.data.config.ConfigHelper.loadConfig;
import static io.github.null2264.cobblegen.util.Constants.OP_LEVEL_GAMEMASTERS;

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
     * @deprecated Only for internal usage. Use the parameter {@link CGRegistry registry} instead to register new Fluid Interaction
     */
    @Deprecated
    @ApiStatus.Internal
    public static final FluidInteraction FLUID_INTERACTION = new FluidInteraction();
    private static final Path configPath = LoaderCompat.getConfigDir();
    private static final File configFile = new File(configPath + File.separator + MOD_ID + ".json5");
    private static final File metaConfigFile = new File(configPath + File.separator + MOD_ID + "-meta.json5");
    @ApiStatus.Internal
    public static ConfigMetaData META_CONFIG = loadConfig(false, metaConfigFile, null, new ConfigMetaData(), ConfigMetaData.class);

    public CobbleGen() {
        // Force config to be generated when loading up the game instead of having to load a world
        loadConfig(false, configFile, null, new ConfigData(), ConfigData.class);
    }

    //#if FABRIC>=1
    @Override
    public void onInitialize() {}
    //#endif

    public static void initCommands(CommandDispatcher<CommandSourceStack> dispatcher) {
        CGLog.info("Registering command...");
        dispatcher.register(
                LiteralArgumentBuilder.<CommandSourceStack>literal("cobblegen")
                        .then(LiteralArgumentBuilder.<CommandSourceStack>literal("reload-meta").requires(arg -> arg.hasPermission(OP_LEVEL_GAMEMASTERS)).executes(c -> {
                            CGLog.info("Reloading meta config...");
                            META_CONFIG = loadConfig(true, metaConfigFile, META_CONFIG, new ConfigMetaData(), ConfigMetaData.class);
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
