package io.github.null2264.cobblegen.integration;

import com.simibubi.create.AllFluids;
import com.simibubi.create.content.palettes.AllPaletteStoneTypes;
import io.github.null2264.cobblegen.CobbleGenPlugin;
import io.github.null2264.cobblegen.config.WeightedBlock;
import io.github.null2264.cobblegen.data.generator.CobbleGenerator;
import io.github.null2264.cobblegen.data.model.CGRegistry;
import io.github.null2264.cobblegen.util.CGLog;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.world.level.material.Fluids;

import java.util.List;

public class CreatePlugin implements CobbleGenPlugin
{
    private boolean firstInit = true;

    @Override
    public void registerInteraction(CGRegistry registry) {
        if (!FabricLoader.getInstance().isModLoaded("create")) return;

        CGLog.info("Create mod detected,", firstInit ? "loading" : "reloading", "integration...");
        registry.addGenerator(
                Fluids.LAVA,
                new CobbleGenerator(
                        List.of(WeightedBlock.fromBlock(AllPaletteStoneTypes.LIMESTONE.getBaseBlock().get(), 1.0)),
                        AllFluids.HONEY.get().getSource(),
                        false
                )
        );
        registry.addGenerator(
                Fluids.LAVA,
                new CobbleGenerator(
                        List.of(WeightedBlock.fromBlock(AllPaletteStoneTypes.SCORIA.getBaseBlock().get(), 1.0)),
                        AllFluids.CHOCOLATE.get().getSource(),
                        false
                )
        );

        if (firstInit) firstInit = false;
    }
}