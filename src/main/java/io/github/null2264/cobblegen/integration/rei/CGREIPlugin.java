package io.github.null2264.cobblegen.integration.rei;

import io.github.null2264.cobblegen.config.WeightedBlock;
import io.github.null2264.cobblegen.util.GeneratorType;
import io.github.null2264.cobblegen.util.Util;
import lombok.val;
import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import me.shedaniel.rei.api.client.registry.category.CategoryRegistry;
import me.shedaniel.rei.api.client.registry.display.DisplayRegistry;
import net.minecraft.block.Blocks;
import net.minecraft.util.Identifier;

import static io.github.null2264.cobblegen.CobbleGen.getCompat;

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
        for (GeneratorType type : GeneratorType.values()) {
            val config = Util.configFromType(type);
            for (WeightedBlock block : config.getLeft()) {
                registry.add(new FluidInteractionRecipeHolderDisplay(
                        block,
                        type,
                        type == GeneratorType.BASALT ? Blocks.SOUL_SOIL : null
                ));
            }
            try {
                config.getRight().forEach((modifierId, blocks) -> {
                    val modifier = getCompat().getBlock(new Identifier(modifierId));
                    for (WeightedBlock block : blocks) {
                        registry.add(new FluidInteractionRecipeHolderDisplay(block, type, modifier));
                    }
                });
            } catch (NullPointerException ignored) {
            }
        }
    }
}