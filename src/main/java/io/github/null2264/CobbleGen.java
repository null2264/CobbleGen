package io.github.null2264;

import io.github.null2264.config.CobbleGenConfig;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.JanksonConfigSerializer;
import net.fabricmc.api.ModInitializer;

public class CobbleGen implements ModInitializer
{
    public static final String MOD_ID = "cobblegen";

    @Override
    public void onInitialize() {
        CobbleGenConfig.init();
    }
}