package io.github.null2264.cobblegen.integration.create;

import com.simibubi.create.AllFluids;
import com.simibubi.create.content.palettes.AllPaletteStoneTypes;
import io.github.null2264.cobblegen.config.WeightedBlock;
import io.github.null2264.cobblegen.data.CobbleGenerator;
import io.github.null2264.cobblegen.util.CobbleGenPlugin;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.fluid.Fluids;

import java.util.List;

import static io.github.null2264.cobblegen.CobbleGen.FLUID_INTERACTION;
import static io.github.null2264.cobblegen.CobbleGen.getCompat;

public class CreateFluidInteraction implements CobbleGenPlugin
{
    public void registerInteraction() {
        if (!FabricLoader.getInstance().isModLoaded("create")) return;
        FLUID_INTERACTION.addGenerator(
                Fluids.LAVA,
                new CobbleGenerator(
                        List.of(new WeightedBlock("create:limestone", 1.0)),
                        getCompat().getFluid(AllFluids.HONEY.getId()),
                        false
                )
        );
        FLUID_INTERACTION.addGenerator(
                Fluids.LAVA,
                new CobbleGenerator(
                        List.of(new WeightedBlock("create:scoria", 1.0)),
                        getCompat().getFluid(AllFluids.CHOCOLATE.getId()),
                        false
                )
        );
    }
}