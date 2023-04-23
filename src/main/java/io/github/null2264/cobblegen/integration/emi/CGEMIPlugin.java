package io.github.null2264.cobblegen.integration.emi;

import dev.emi.emi.api.EmiPlugin;
import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.stack.EmiStack;
import io.github.null2264.cobblegen.config.WeightedBlock;
import io.github.null2264.cobblegen.util.GeneratorType;
import io.github.null2264.cobblegen.util.Util;
import lombok.val;
import net.minecraft.block.Blocks;
import net.minecraft.util.Identifier;

import java.util.Map;

import static io.github.null2264.cobblegen.CobbleGen.getCompat;

public class CGEMIPlugin implements EmiPlugin
{
    public static final String ID_PREFIX = "fluid_interaction_";
    public static final Map<String, EmiRecipeCategory> FLUID_INTERACTION = Map.of("COBBLE",
                                                                                  new EmiRecipeCategory(Util.identifierOf(
                                                                                          ID_PREFIX + "cobble"),
                                                                                                        EmiStack.of(
                                                                                                                Blocks.COBBLESTONE)
                                                                                  ),
                                                                                  "STONE",
                                                                                  new EmiRecipeCategory(Util.identifierOf(
                                                                                          ID_PREFIX + "stone"),
                                                                                                        EmiStack.of(
                                                                                                                Blocks.STONE)
                                                                                  ),
                                                                                  "BASALT",
                                                                                  new EmiRecipeCategory(Util.identifierOf(
                                                                                          ID_PREFIX + "basalt"),
                                                                                                        EmiStack.of(
                                                                                                                Blocks.BASALT)
                                                                                  )
    );

    @Override
    public void register(EmiRegistry registry) {
        FLUID_INTERACTION.forEach((ignored, category) -> registry.addCategory(category));

        for (GeneratorType type : GeneratorType.values()) {
            val config = Util.configFromType(type);
            for (WeightedBlock block : config.getLeft()) {
                registry.addRecipe(new FluidInteractionCategory(block,
                                                                type,
                                                                type == GeneratorType.BASALT ? Blocks.SOUL_SOIL : null
                ));
            }
            try {
                config.getRight().forEach((modifierId, blocks) -> {
                    val modifier = getCompat().getBlock(new Identifier(modifierId));
                    for (WeightedBlock block : blocks) {
                        registry.addRecipe(new FluidInteractionCategory(block, type, modifier));
                    }
                });
            } catch (NullPointerException ignored) {
            }
        }
    }
}