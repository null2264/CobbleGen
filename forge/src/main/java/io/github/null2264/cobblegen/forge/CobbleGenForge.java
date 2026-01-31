package io.github.null2264.cobblegen.fabric;

import io.github.null2264.cobblegen.CobbleGen;

#if FORGE
    #if FORGE==2
@net.neoforged.fml.common.Mod(CobbleGen.MOD_ID)
    #elif FORGE==1
@net.minecraftforge.fml.common.Mod(CobbleGen.MOD_ID)
    #endif
#endif
public class CobbleGenForge extends CobbleGen {

    public CobbleGenForge {
        #if MC>=11801 && MC<12105
        // I was gonna do RegisterGameTestsEvent like a normal person, but there's a check that I need to bypass otherwise Forge won't register my test
        net.minecraft.gametest.framework.GameTestRegistry.register(io.github.null2264.cobblegen.gametest.BlockGenerationTest.class);
        #endif
    }
}
