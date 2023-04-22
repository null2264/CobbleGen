package io.github.null2264.cobblegen.integration.rei;

import io.github.null2264.cobblegen.config.WeightedBlock;
import io.github.null2264.cobblegen.integration.FluidInteractionRecipeHolder;
import io.github.null2264.cobblegen.util.GeneratorType;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.Display;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.util.EntryStacks;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.fluid.Fluids;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class FluidInteractionRecipeHolderDisplay extends FluidInteractionRecipeHolder implements Display
{
    public FluidInteractionRecipeHolderDisplay(
            WeightedBlock result, GeneratorType type, @Nullable Block modifier
    ) {
        super(result, type, modifier);
    }

    @Override
    public List<EntryIngredient> getInputEntries() {
        switch (getType()) {
            case COBBLE, STONE -> {
                return List.of(
                        EntryIngredient.of(EntryStacks.of(Fluids.LAVA)),
                        EntryIngredient.of(EntryStacks.of(Fluids.WATER)),
                        EntryIngredient.of(EntryStacks.of(getModifier()))
                );
            }
            case BASALT -> {
                return List.of(
                        EntryIngredient.of(EntryStacks.of(Fluids.LAVA)),
                        EntryIngredient.of(EntryStacks.of(Blocks.BLUE_ICE)),
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