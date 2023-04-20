package io.github.null2264.cobblegen.integration.jei;

import io.github.null2264.cobblegen.util.GeneratorType;
import lombok.val;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableStatic;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.block.Blocks;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;

import static io.github.null2264.cobblegen.util.Util.identifierOf;

public class FluidInteractionCategory implements IRecipeCategory<FluidInteractionRecipeHolder>
{
    private final IDrawableStatic background;
    private final IDrawable icon;
    private final GeneratorType type;

    public FluidInteractionCategory(IGuiHelper guiHelper, GeneratorType generatorType) {
        background = guiHelper.createBlankDrawable(52, 32);
        ItemStack iconStack = Items.AIR.getDefaultStack();
        switch (generatorType) {
            case COBBLE -> iconStack = Items.COBBLESTONE.getDefaultStack();
            case STONE -> iconStack = Items.STONE.getDefaultStack();
            case BASALT -> iconStack = Items.BASALT.getDefaultStack();
        }
        icon = guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK, iconStack);
        type = generatorType;
    }

    @Override
    public @NotNull RecipeType<FluidInteractionRecipeHolder> getRecipeType() {
        return new RecipeType<>(identifierOf(type), FluidInteractionRecipeHolder.class);
    }

    @Override
    public @NotNull Text getTitle() {
        return Text.translatable("cobblegen.generators." + type.name().toLowerCase());
    }

    @Override
    public @NotNull IDrawable getBackground() {
        return background;
    }

    @Override
    public @NotNull IDrawable getIcon() {
        return icon;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, FluidInteractionRecipeHolder recipe, IFocusGroup focuses) {
        val output = recipe.getResult();
        val modifier = recipe.getModifier();

        var offset = 18;
        var x = 0;
        var y = 0;

        var isBasaltGen = false;
        var coldX = x;
        var coldY = y;

        var lavaX = x + 2 * offset;

        var resultX = x + offset - 1;
        var resultY = y - 1;
        var resultModY = y + offset - 1;

        switch (type) {
            case STONE -> {
                lavaX = x + offset;
                coldY = y + offset;
                resultY = resultModY;
                resultModY = y + 2 * offset - 1;
            }
            case BASALT -> {
                isBasaltGen = true;
                coldX = lavaX;
                coldY = y - 1;
                lavaX = x;
            }
        }

        val coldBuilder = builder.addSlot(RecipeIngredientRole.INPUT, coldX, coldY);
        if (isBasaltGen)
            coldBuilder.addItemStack(Blocks.BLUE_ICE.asItem().getDefaultStack());
        else
            coldBuilder.addFluidStack(Fluids.WATER, 1000L);
        builder.addSlot(RecipeIngredientRole.INPUT, lavaX, y).addFluidStack(Fluids.LAVA, 1000L);
        builder.addSlot(RecipeIngredientRole.OUTPUT, resultX, resultY).addItemStack(output.getBlock().asItem().getDefaultStack());
        if (modifier != null)
            builder.addSlot(RecipeIngredientRole.INPUT, resultX, resultModY).addItemStack(modifier.asItem().getDefaultStack());
    }
}