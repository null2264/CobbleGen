package io.github.null2264.cobblegen.integration.viewer.emi;

import dev.emi.emi.api.EmiPlugin;
import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.stack.EmiStack;
import io.github.null2264.cobblegen.config.WeightedBlock;
import io.github.null2264.cobblegen.util.Util;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

import java.util.Map;
import java.util.Objects;

import static io.github.null2264.cobblegen.CobbleGen.FLUID_INTERACTION;

public class CGEMIPlugin implements EmiPlugin
{
    public static final String ID_PREFIX = "fluid_interaction_";
    public static final Map<String, EmiRecipeCategory> FLUID_INTERACTION_CATEGORIES = Map.of(
            "COBBLE",
            new EmiRecipeCategory(
                    Util.identifierOf(
                            ID_PREFIX + "cobble"),
                    EmiStack.of(
                            Blocks.COBBLESTONE)
            ),
            "STONE",
            new EmiRecipeCategory(
                    Util.identifierOf(
                            ID_PREFIX + "stone"),
                    EmiStack.of(
                            Blocks.STONE)
            ),
            "BASALT",
            new EmiRecipeCategory(
                    Util.identifierOf(
                            ID_PREFIX + "basalt"),
                    EmiStack.of(
                            Blocks.BASALT)
            )
    );

    @Override
    public void register(EmiRegistry registry) {
        FLUID_INTERACTION_CATEGORIES.forEach((ignored, category) -> registry.addCategory(category));
        FLUID_INTERACTION.getGenerators().forEach((fluid, generators) -> generators.forEach(generator -> generator.getOutput().forEach(
                (modifierId, blocks) -> {
                    Block modifier = null;
                    if (!Objects.equals(modifierId, "*"))
                        modifier = Util.getBlock(new ResourceLocation(modifierId));
                    for (WeightedBlock block : blocks)
                        registry.addRecipe(
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