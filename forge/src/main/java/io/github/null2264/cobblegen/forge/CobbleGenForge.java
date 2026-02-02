package io.github.null2264.cobblegen.fabric;

import io.github.null2264.cobblegen.CobbleGen;

import static io.github.null2264.cobblegen.CobbleGen.MOD_ID;

#if MC>=12002
@net.neoforged.fml.common.Mod(MOD_ID)
#else
@net.minecraftforge.fml.common.Mod(MOD_ID)
#endif
public class CobbleGenForge extends CobbleGen {

    public CobbleGenForge() {
        super.init();
        #if MC>=11801 && MC<12105
        // I was gonna do RegisterGameTestsEvent like a normal person, but there's a check that I need to bypass otherwise Forge won't register my test
        net.minecraft.gametest.framework.GameTestRegistry.register(io.github.null2264.cobblegen.gametest.BlockGenerationTest.class);
        #endif
    }
}
