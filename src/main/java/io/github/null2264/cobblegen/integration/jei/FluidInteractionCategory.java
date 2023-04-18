package io.github.null2264.cobblegen.integration.jei;

import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;

public class FluidInteractionCategory implements IRecipeCategory<FluidInteractionDisplay>
{
    @Override
    public @NotNull RecipeType<FluidInteractionDisplay> getRecipeType() {
        return null;
    }

    @Override
    public @NotNull Text getTitle() {
        return null;
    }

    @Override
    public @NotNull IDrawable getBackground() {
        return null;
    }

    @Override
    public @NotNull IDrawable getIcon() {
        return null;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, FluidInteractionDisplay recipe, IFocusGroup focuses) {

    }
}