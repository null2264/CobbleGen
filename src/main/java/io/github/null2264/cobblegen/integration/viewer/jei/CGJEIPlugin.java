//#if MC>1.16.5
package io.github.null2264.cobblegen.integration.viewer.jei;

import io.github.null2264.cobblegen.CobbleGen;
import io.github.null2264.cobblegen.data.config.WeightedBlock;
import io.github.null2264.cobblegen.integration.viewer.FluidInteractionRecipeHolder;
import io.github.null2264.cobblegen.util.GeneratorType;
import io.github.null2264.cobblegen.util.Util;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.helpers.IJeiHelpers;
import mezz.jei.api.helpers.IPlatformFluidHelper;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.material.Fluids;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import static io.github.null2264.cobblegen.CobbleGen.FLUID_INTERACTION;
import static io.github.null2264.cobblegen.util.Util.identifierOf;

@JeiPlugin
public class CGJEIPlugin implements IModPlugin
{
    @Override
    public @NotNull ResourceLocation getPluginUid() {
        return identifierOf("plugin");
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        if (!CobbleGen.META_CONFIG.enableRecipeViewer)
            return;

        IJeiHelpers jeiHelpers = registration.getJeiHelpers();
        IGuiHelper guiHelper = jeiHelpers.getGuiHelper();
        IPlatformFluidHelper<?> fluidHelper = jeiHelpers.getPlatformFluidHelper();

        for (GeneratorType generator : GeneratorType.values()) {
            FluidInteractionCategory category = new FluidInteractionCategory(guiHelper, fluidHelper, generator);
            registration.addRecipeCategories(category);
        }
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        if (!CobbleGen.META_CONFIG.enableRecipeViewer)
            return;

        FLUID_INTERACTION.getGenerators().forEach((fluid, generators) -> generators.forEach(generator -> generator.getOutput().forEach(
                (modifierId, blocks) -> {
                    final ArrayList<FluidInteractionRecipeHolder> recipes = new ArrayList<>();
                    Block modifier = null;
                    if (!modifierId.isWildcard())
                        modifier = Util.getBlock(modifierId.toMC());
                    for (WeightedBlock block : blocks)
                        recipes.add(
                                new FluidInteractionRecipeHolder(
                                        fluid,
                                        Util.notNullOr(generator.getFluid(), Fluids.EMPTY),
                                        Util.notNullOr(generator.getBlock(), Blocks.AIR),
                                        block,
                                        generator.getType(),
                                        Util.notNullOr(modifier, Blocks.AIR)
                                )
                        );
                    registration.addRecipes(new RecipeType<>(
                            identifierOf(generator.getType()),
                            FluidInteractionRecipeHolder.class
                    ), recipes);
                })));
    }
}
//#endif