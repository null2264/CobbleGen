package io.github.null2264.cobblegen;

import io.github.null2264.cobblegen.config.ConfigHelper;
import net.fabricmc.api.ModInitializer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CobbleGen implements ModInitializer
{
    public static final Logger LOGGER = LogManager.getLogger("CobbleGen");
    public static final String MOD_ID = "cobblegen";

    @Override
    public void onInitialize() {
        LOGGER.info("Loading config...");
        ConfigHelper.loadAndSaveDefault();
    }
}