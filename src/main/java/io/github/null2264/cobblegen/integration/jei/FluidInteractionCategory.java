package io.github.null2264.cobblegen.integration.jei;

import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;

public class FluidInteractionCategory implements IRecipeCategory<FluidInteractionDisplay>
{
    @Override
    public RecipeType<FluidInteractionDisplay> getRecipeType() {
        return null;
    }

    @Override
    public net.minecraft.network.chat.Component getTitle() {
        return null;
    }

    @Override
    public IDrawable getBackground() {
        return null;
    }

    @Override
    public IDrawable getIcon() {
        return null;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, FluidInteractionDisplay recipe, IFocusGroup focuses) {

    }
}