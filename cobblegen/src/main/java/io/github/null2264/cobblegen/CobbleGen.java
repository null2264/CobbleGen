package io.github.null2264.cobblegen;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.github.null2264.cobblegen.compat.LoaderCompat;
import io.github.null2264.cobblegen.compat.ModContainerCompat;
import io.github.null2264.cobblegen.compat.TextCompat;
import io.github.null2264.cobblegen.data.config.ConfigData;
import io.github.null2264.cobblegen.data.config.ConfigMetaData;
import io.github.null2264.cobblegen.data.model.CGRegistry;
import io.github.null2264.cobblegen.util.CGLog;
import net.minecraft.commands.CommandSourceStack;
import org.jetbrains.annotations.ApiStatus;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.regex.PatternSyntaxException;

import static io.github.null2264.cobblegen.data.config.ConfigHelper.loadConfig;
import static io.github.null2264.cobblegen.util.Constants.OP_LEVEL_GAMEMASTERS;

#if FORGE
    #if FORGE==2
@net.neoforged.fml.common.Mod(CobbleGen.MOD_ID)
    #elif FORGE==1
@net.minecraftforge.fml.common.Mod(CobbleGen.MOD_ID)
    #endif
#endif
public class CobbleGen
#if FABRIC
    implements net.fabricmc.api.ModInitializer
#endif
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
        loadConfig(false, configFile, null, ConfigData.defaultConfig(), ConfigData.class);
        #if FORGE && MC>=11801 && MC<12105
        // I was gonna do RegisterGameTestsEvent like a normal person, but there's a check that I need to bypass otherwise Forge won't register my test
        net.minecraft.gametest.framework.GameTestRegistry.register(io.github.null2264.cobblegen.gametest.BlockGenerationTest.class);
        #endif
    }

    #if FABRIC
    @Override
    public void onInitialize() {}
    #endif

    public static void initCommands(CommandDispatcher<CommandSourceStack> dispatcher) {
        CGLog.info("Registering command...");
        dispatcher.register(
                LiteralArgumentBuilder.<CommandSourceStack>literal("cobblegen")
                        .then(LiteralArgumentBuilder.<CommandSourceStack>literal("reload-meta").requires(arg -> arg.hasPermission(OP_LEVEL_GAMEMASTERS)).executes(c -> {
                            CGLog.info("Reloading meta config...");
                            META_CONFIG = loadConfig(true, metaConfigFile, META_CONFIG, new ConfigMetaData(), ConfigMetaData.class);
                            c.getSource().sendSuccess(
                                #if MC>=12001
                                () ->
                                #endif
                                TextCompat.literal("Meta config has been reloaded"), false
                            );
                            CGLog.info("Meta config has been reloaded");
                            return 0;
                        }))
        );
    }

    @ApiStatus.Internal
    public enum CreateSupport {
        NONE,
        O_FIVE_ONE_E,
        O_FIVE_ONE_F,
        SIX_O,  // FIXME: Properly support 6.0.x
    }

    /**
     * Check Create mod version. Returns integer, in case Create introduced yet another breaking change
     */
    @ApiStatus.Internal
    public static CobbleGen.CreateSupport getCreateSupport() {
        if (CobbleGen.META_CONFIG.create.loadIntegration) return CobbleGen.CreateSupport.NONE;

        #if MC<=11605
        return CobbleGen.CreateSupport.NONE;  // We don't support create integration for MC1.16.5
        #else
        try {
            String version = ModContainerCompat.fromLoader("create").getVersionString();
            ArrayList<Integer> split = new ArrayList<>();
            String patch = "a";
            // version = 0.5.1-a-build.69
            for (String s : version.split("\\.")) {
                try {
                    // 0.5.
                    split.add(Integer.valueOf(s));
                } catch (NumberFormatException exc) {
                    // 1a or 1-a-build -> 1
                    split.add(Integer.valueOf(s.substring(0, 1)));
                    // -a-build -> '', a, build | a-build -> a, build
                    String[] patchSplit = s.substring(1).split("-");
                    if (patchSplit[0].equalsIgnoreCase("")) {
                        patch = patchSplit[1];
                    } else {
                        // For older version that use 0.5.0a format instead of 0.5.0-a
                        patch = patchSplit[0];
                    }
                    break;
                }
            }
            if (split.get(0) == 0) {  // 0.x.x
                #if FORGE
                return CobbleGen.CreateSupport.O_FIVE_ONE_E;  // Always use Patch E mixin for Forge-alike
                #else
                if (split.get(1) == 5 && split.get(2) == 0 || split.get(1) < 5) return CobbleGen.CreateSupport.O_FIVE_ONE_E;
                if (split.get(1) > 5 || split.get(2) > 1) return CobbleGen.CreateSupport.O_FIVE_ONE_F;  // Assume they don't introduce breaking changes on version bump

                int compare = patch.compareToIgnoreCase("e");
                if (compare <= 0) return CobbleGen.CreateSupport.O_FIVE_ONE_E;
                return CobbleGen.CreateSupport.O_FIVE_ONE_F;
                #endif
            } else if (split.get(0) == 6) {  // 6.x.x
                // TODO
                CGLog.warn("Create mod v6+ is not yet supported");
                return CobbleGen.CreateSupport.NONE;
            }
            // They're definitely going to introduce breaking changes on major version bump
            return CobbleGen.CreateSupport.NONE;
        } catch (java.util.NoSuchElementException | PatternSyntaxException | NumberFormatException exc) {
            CGLog.error(exc);
            return CobbleGen.CreateSupport.NONE;
        }
        #endif
    }
}
