package io.github.null2264.cobblegen;

import io.github.null2264.cobblegen.data.FluidInteractionHelper;
import io.github.null2264.cobblegen.util.Compat;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CobbleGen implements ModInitializer
{
    public static final Logger LOGGER = LogManager.getLogger("CobbleGen");
    public static final String MOD_ID = "cobblegen";
    public static final FluidInteractionHelper FLUID_INTERACTION = new FluidInteractionHelper();
    private static Compat compat;

    public static Compat getCompat() {
        if (compat == null) compat = FabricLoader.getInstance().getEntrypoints("cobblegen-compat", Compat.class).get(0);
        return compat;
    }

    @Override
    public void onInitialize() {
        LOGGER.info("Loading config...");
        FLUID_INTERACTION.apply();
    }
}