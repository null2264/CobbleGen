package io.github.null2264.cobblegen.integration.jei;

import io.github.null2264.cobblegen.config.WeightedBlock;
import io.github.null2264.cobblegen.util.GeneratorType;
import io.github.null2264.cobblegen.util.Util;
import lombok.val;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.block.Blocks;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

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
        FluidInteractionCategory cobbleGen = new FluidInteractionCategory(registration.getJeiHelpers().getGuiHelper(), GeneratorType.COBBLE);
        FluidInteractionCategory stoneGen = new FluidInteractionCategory(registration.getJeiHelpers().getGuiHelper(), GeneratorType.STONE);
        FluidInteractionCategory basaltGen = new FluidInteractionCategory(registration.getJeiHelpers().getGuiHelper(), GeneratorType.BASALT);
        registration.addRecipeCategories(cobbleGen);
        registration.addRecipeCategories(stoneGen);
        registration.addRecipeCategories(basaltGen);
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        for (GeneratorType type : GeneratorType.values()) {
            val config = Util.configFromType(type);
            val recipes = new ArrayList<FluidInteractionRecipeHolder>();
            for (WeightedBlock block : config.getLeft()) {
                recipes.add(
                        new FluidInteractionRecipeHolder(block, type, type == GeneratorType.BASALT ? Blocks.SOUL_SOIL : null)
                );
            }
            try {
                config.getRight().forEach((modifierId, blocks) -> {
                    val modifier = getCompat().getBlock(new Identifier(modifierId));
                    for (WeightedBlock block : blocks) {
                        recipes.add(
                                new FluidInteractionRecipeHolder(block, type, modifier)
                        );
                    }
                });
            } catch (NullPointerException ignored) {
            }
            registration.addRecipes(new RecipeType<>(identifierOf(type), FluidInteractionRecipeHolder.class), recipes);
        }
    }
}