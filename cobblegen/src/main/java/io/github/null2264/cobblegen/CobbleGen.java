package io.github.null2264.cobblegen;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.github.null2264.cobblegen.compat.TextCompat;
import io.github.null2264.cobblegen.data.config.ConfigData;
import io.github.null2264.cobblegen.data.config.ConfigMetaData;
import io.github.null2264.cobblegen.data.model.CGRegistry;
import io.github.null2264.cobblegen.util.CGLog;
import net.minecraft.commands.CommandSourceStack;
import org.jetbrains.annotations.ApiStatus;

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

    public CobbleGen() {
        // Force config to be generated when loading up the game instead of having to load a world
        new ConfigData.Factory().load();
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
                            ConfigMetaData.INSTANCE = new ConfigMetaData.Factory().reload(ConfigMetaData.INSTANCE);
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
}
