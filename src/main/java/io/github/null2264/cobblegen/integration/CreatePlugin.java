//#if MC>1.16.5
package io.github.null2264.cobblegen.integration;

import io.github.null2264.cobblegen.CGPlugin;
import io.github.null2264.cobblegen.CobbleGenPlugin;
import io.github.null2264.cobblegen.compat.LoaderCompat;
import io.github.null2264.cobblegen.config.WeightedBlock;
import io.github.null2264.cobblegen.data.generator.CobbleGenerator;
import io.github.null2264.cobblegen.data.model.CGRegistry;
import io.github.null2264.cobblegen.util.CGLog;
import io.github.null2264.cobblegen.util.Util;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.material.Fluids;

import static io.github.null2264.cobblegen.compat.CollectionCompat.listOf;

@CGPlugin
public class CreatePlugin implements CobbleGenPlugin
{
    private boolean firstInit = true;

    @Override
    public void registerInteraction(CGRegistry registry) {
        if (!LoaderCompat.isModLoaded("create")) return;

        CGLog.info("Create mod detected,", firstInit ? "loading" : "reloading", "integration...");
        registry.addGenerator(
                Fluids.LAVA,
                new CobbleGenerator(
                        listOf(WeightedBlock.fromBlock(Util.getBlock(new ResourceLocation("create", "limestone")), 1.0)),
                        Util.getFluid(new ResourceLocation("create", "honey")),
                        false
                )
        );
        registry.addGenerator(
                Fluids.LAVA,
                new CobbleGenerator(
                        listOf(WeightedBlock.fromBlock(Util.getBlock(new ResourceLocation("create", "scoria")), 1.0)),
                        Util.getFluid(new ResourceLocation("create", "chocolate")),
                        false
                )
        );

        if (firstInit) firstInit = false;
    }
}
//#endif