package io.github.null2264.cobblegen.fabric;

import io.github.null2264.cobblegen.CobbleGen;
import io.github.null2264.cobblegen.compat.LoaderCompat;
import io.github.null2264.cobblegen.forge.compat.LoaderCompatForge;
import io.github.null2264.cobblegen.forge.util.PluginFinderForge;
import io.github.null2264.cobblegen.util.PluginFinder;

#if MC>=12002
@net.neoforged.fml.common.Mod(CobbleGen.MOD_ID)
#else
@net.minecraftforge.fml.common.Mod(CobbleGen.MOD_ID)
#endif
public class CobbleGenForge extends CobbleGen {

    public CobbleGenForge() {
        LoaderCompat.init(new LoaderCompatForge());
        PluginFinder.init(new PluginFinderForge());
        #if MC>=11801 && MC<12105
        // I was gonna do RegisterGameTestsEvent like a normal person, but there's a check that I need to bypass otherwise Forge won't register my test
        net.minecraft.gametest.framework.GameTestRegistry.register(io.github.null2264.cobblegen.gametest.BlockGenerationTest.class);
        #endif
    }
}
