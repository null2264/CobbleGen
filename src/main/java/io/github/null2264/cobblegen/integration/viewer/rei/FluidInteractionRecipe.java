//#if MC>1.16.5
package io.github.null2264.cobblegen.integration.viewer.rei;

import io.github.null2264.cobblegen.config.WeightedBlock;
import io.github.null2264.cobblegen.integration.viewer.FluidInteractionRecipeHolder;
import io.github.null2264.cobblegen.util.GeneratorType;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.Display;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.util.EntryStacks;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;

import java.util.List;

public class FluidInteractionRecipe extends FluidInteractionRecipeHolder implements Display
{
    public FluidInteractionRecipe(
            Fluid sourceFluid,
            Fluid neighbourFluid,
            Block neighbourBlock,
            WeightedBlock result,
            GeneratorType type,
            Block modifier
    ) {
        super(sourceFluid, neighbourFluid, neighbourBlock, result, type, modifier);
    }

    @Override
    public List<EntryIngredient> getInputEntries() {
        switch (getType()) {
            case COBBLE, STONE -> {
                return List.of(
                        EntryIngredient.of(EntryStacks.of(getSourceFluid())),
                        EntryIngredient.of(EntryStacks.of(getNeighbourFluid())),
                        EntryIngredient.of(EntryStacks.of(getModifier()))
                );
            }
            case BASALT -> {
                return List.of(
                        EntryIngredient.of(EntryStacks.of(getSourceFluid())),
                        EntryIngredient.of(EntryStacks.of(getNeighbourBlock())),
                        EntryIngredient.of(EntryStacks.of(getModifier()))
                );
            }
        }
        return List.of();
    }

    @Override
    public List<EntryIngredient> getOutputEntries() {
        return List.of(EntryIngredient.of(EntryStacks.of(getResult().getBlock())));
    }

    @Override
    public CategoryIdentifier<?> getCategoryIdentifier() {
        return FluidInteractionCategory.generateIdentifier(getType());
    }
}
//#endif