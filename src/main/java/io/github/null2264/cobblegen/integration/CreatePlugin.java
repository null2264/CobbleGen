package io.github.null2264.cobblegen.integration;

import com.simibubi.create.AllFluids;
import com.simibubi.create.content.palettes.AllPaletteStoneTypes;
import io.github.null2264.cobblegen.config.WeightedBlock;
import io.github.null2264.cobblegen.data.generator.CobbleGenerator;
import io.github.null2264.cobblegen.util.CGLog;
import io.github.null2264.cobblegen.util.CobbleGenPlugin;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.fluid.Fluids;

import java.util.List;

import static io.github.null2264.cobblegen.CobbleGen.FLUID_INTERACTION;

public class CreatePlugin implements CobbleGenPlugin
{
    private boolean firstInit = true;

    @Override
    public void registerInteraction() {
        if (!FabricLoader.getInstance().isModLoaded("create")) return;

        CGLog.info("Create mod detected,", firstInit ? "loading" : "reloading", "integration...");
        FLUID_INTERACTION.addGenerator(
                Fluids.LAVA,
                new CobbleGenerator(
                        List.of(WeightedBlock.fromBlock(AllPaletteStoneTypes.LIMESTONE.getBaseBlock().get(), 1.0)),
                        AllFluids.HONEY.get().getStill(),
                        false
                )
        );
        FLUID_INTERACTION.addGenerator(
                Fluids.LAVA,
                new CobbleGenerator(
                        List.of(WeightedBlock.fromBlock(AllPaletteStoneTypes.SCORIA.getBaseBlock().get(), 1.0)),
                        AllFluids.CHOCOLATE.get().getStill(),
                        false
                )
        );

        if (firstInit) firstInit = false;
    }
}