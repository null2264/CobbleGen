package io.github.null2264.cobblegen.integration.viewer.jei;

import io.github.null2264.cobblegen.config.WeightedBlock;
import io.github.null2264.cobblegen.integration.viewer.FluidInteractionRecipeHolder;
import io.github.null2264.cobblegen.util.GeneratorType;
import lombok.val;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.helpers.IJeiHelpers;
import mezz.jei.api.helpers.IPlatformFluidHelper;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.block.Block;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Objects;

import static io.github.null2264.cobblegen.CobbleGen.FLUID_INTERACTION;
import static io.github.null2264.cobblegen.CobbleGen.getCompat;
import static io.github.null2264.cobblegen.util.Util.identifierOf;

@JeiPlugin
public class CGJEIPlugin implements IModPlugin
{
    @Override
    public @NotNull Identifier getPluginUid() {
        return identifierOf("plugin");
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
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
        FLUID_INTERACTION.getGenerators().forEach((fluid, generators) -> generators.forEach(generator -> generator.getOutput().forEach(
                (modifierId, blocks) -> {
                    val recipes = new ArrayList<FluidInteractionRecipeHolder>();
                    Block modifier = null;
                    if (!Objects.equals(modifierId, "*"))
                        modifier = getCompat().getBlock(new Identifier(modifierId));
                    for (WeightedBlock block : blocks)
                        recipes.add(
                                new FluidInteractionRecipeHolder(
                                        fluid,
                                        generator.getFluid(),
                                        generator.getBlock(),
                                        block,
                                        generator.getType(),
                                        modifier
                                )
                        );
                    registration.addRecipes(new RecipeType<>(
                            identifierOf(generator.getType()),
                            FluidInteractionRecipeHolder.class
                    ), recipes);
                })));
    }
}