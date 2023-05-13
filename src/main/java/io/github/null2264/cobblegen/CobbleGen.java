package io.github.null2264.cobblegen;

import io.github.null2264.cobblegen.data.FluidInteractionHelper;
import io.github.null2264.cobblegen.util.CobbleGenPlugin;
import io.github.null2264.cobblegen.util.Compat;
import io.github.null2264.cobblegen.util.PluginFinder;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Logger;
import org.apache.logging.log4j.core.config.Configurator;

public class CobbleGen implements ModInitializer
{
    public static final Logger LOGGER = (Logger) LogManager.getLogger("CobbleGen");
    public static final String MOD_ID = "cobblegen";
    public static final Identifier SYNC_CHANNEL = new Identifier(MOD_ID, "sync");
    public static final Identifier SYNC_PING_CHANNEL = new Identifier(MOD_ID, "sync_ping");
    public static final FluidInteractionHelper FLUID_INTERACTION = new FluidInteractionHelper();
    private static Compat compat;

    public static Compat getCompat() {
        if (compat == null) compat = FabricLoader.getInstance().getEntrypoints("cobblegen-compat", Compat.class).get(0);
        return compat;
    }

    @Override
    public void onInitialize() {
        Configurator.setAllLevels(LOGGER.getName(), Level.ALL);
        PluginFinder.getModPlugins().forEach(CobbleGenPlugin::registerInteraction);
        FLUID_INTERACTION.apply();
    }

    public enum Channel {
        PING,
        SYNC,
    }
}