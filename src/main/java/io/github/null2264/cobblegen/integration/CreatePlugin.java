package io.github.null2264.cobblegen.integration;

import io.github.null2264.cobblegen.CGPlugin;
import io.github.null2264.cobblegen.CobbleGen;
import io.github.null2264.cobblegen.CobbleGenPlugin;
import io.github.null2264.cobblegen.compat.LoaderCompat;
import io.github.null2264.cobblegen.data.config.ResultList;
import io.github.null2264.cobblegen.data.config.WeightedBlock;
import io.github.null2264.cobblegen.data.generator.CobbleGenerator;
import io.github.null2264.cobblegen.data.model.CGRegistry;
import io.github.null2264.cobblegen.util.CGLog;
import io.github.null2264.cobblegen.util.Util;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.material.Fluids;

@CGPlugin
public class CreatePlugin implements CobbleGenPlugin
{
    private boolean firstInit = true;

    @Override
    public void registerInteraction(CGRegistry registry) {
        CGLog.info("Create mod detected,", firstInit ? "loading" : "reloading", "integration...");
        registry.addGenerator(
                Fluids.LAVA,
                new CobbleGenerator(
                        ResultList.of(WeightedBlock.fromBlock(Util.getBlock(new ResourceLocation("create", "limestone")), 1.0)),
                        Util.getFluid(new ResourceLocation("create", "honey")),
                        false
                )
        );
        registry.addGenerator(
                Fluids.LAVA,
                new CobbleGenerator(
                        ResultList.of(WeightedBlock.fromBlock(Util.getBlock(new ResourceLocation("create", "scoria")), 1.0)),
                        Util.getFluid(new ResourceLocation("create", "chocolate")),
                        false
                )
        );

        if (firstInit) firstInit = false;
    }

    @Override
    public boolean shouldLoad() {
        return LoaderCompat.isModLoaded("create") && CobbleGen.META_CONFIG.create.loadIntegration;
    }
}