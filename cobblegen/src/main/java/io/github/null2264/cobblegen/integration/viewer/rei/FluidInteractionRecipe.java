#if MC>11605
package io.github.null2264.cobblegen.integration.viewer.rei;

#if MC>=12102
import me.shedaniel.rei.api.common.display.DisplaySerializer;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
#endif

import io.github.null2264.cobblegen.data.config.WeightedBlock;
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

    #if MC>=12102
    @Override
    public Optional<
        net.minecraft.resources.
        #if MC>=12111
        Identifier
        #else
        ResourceLocation
        #endif
    > getDisplayLocation() {
        return Optional.empty();
    }

    @Nullable
    @Override
    public DisplaySerializer<? extends Display> getSerializer() {
        // We never register Display to server, should be fine to leave it null. Tho I should probably do that...
        // REF: https://hackmd.io/@shedaniel/rei17_primer
        return null;
    }
    #endif
}
#endif
