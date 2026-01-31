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

public abstract class CobbleGen {

    public static final String MOD_ID = "cobblegen";
    /**
     * @deprecated Only for internal usage. Use the parameter {@link CGRegistry registry} instead to register new Fluid Interaction
     */
    @Deprecated
    @ApiStatus.Internal
    public static final FluidInteraction FLUID_INTERACTION = new FluidInteraction();

    /*
     * Force config to be generated when loading up the game instead of having to load a world
     */
    public void init() {
        new ConfigData.Factory().load();
    }

    public static void initCommands(CommandDispatcher<CommandSourceStack> dispatcher) {
        CGLog.info("Registering command...");
        dispatcher.register(
                LiteralArgumentBuilder.<CommandSourceStack>literal("cobblegen")
                        .then(
                            LiteralArgumentBuilder.
                                <CommandSourceStack>literal("reload-meta")
                                #if MC<12111
                                .requires(arg -> arg.hasPermission(io.github.null2264.cobblegen.util.Constants.OP_LEVEL_GAMEMASTERS))
                                #else
                                .requires(net.minecraft.commands.Commands.hasPermission(net.minecraft.commands.Commands.LEVEL_GAMEMASTERS))
                                #endif
                                .executes(c -> {
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
                            })
                        )
        );
    }
}
