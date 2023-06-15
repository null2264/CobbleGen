package io.github.null2264.cobblegen.integration.viewer.rei;

import io.github.null2264.cobblegen.config.WeightedBlock;
import io.github.null2264.cobblegen.util.GeneratorType;
import io.github.null2264.cobblegen.util.Util;
import lombok.val;
import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import me.shedaniel.rei.api.client.registry.category.CategoryRegistry;
import me.shedaniel.rei.api.client.registry.display.DisplayRegistry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;

import java.util.Objects;

import static io.github.null2264.cobblegen.CobbleGen.FLUID_INTERACTION;

public class CGREIPlugin implements REIClientPlugin
{
    @Override
    public void registerCategories(CategoryRegistry registry) {
        for (GeneratorType generator : GeneratorType.values()) {
            val category = new FluidInteractionCategory(generator);
            registry.add(category);
        }
    }

    @Override
    public void registerDisplays(DisplayRegistry registry) {
        FLUID_INTERACTION.getGenerators().forEach((fluid, generators) -> generators.forEach(generator -> generator.getOutput().forEach(
                (modifierId, blocks) -> {
                    Block modifier = null;
                    if (!Objects.equals(modifierId, "*"))
                        modifier = Util.getBlock(new ResourceLocation(modifierId));
                    for (WeightedBlock block : blocks)
                        registry.add(
                                new FluidInteractionRecipe(
                                        fluid,
                                        generator.getFluid(),
                                        generator.getBlock(),
                                        block,
                                        generator.getType(),
                                        modifier
                                )
                        );
                })));
    }
}