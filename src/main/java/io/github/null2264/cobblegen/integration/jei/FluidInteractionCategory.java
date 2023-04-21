package io.github.null2264.cobblegen.integration.jei;

import io.github.null2264.cobblegen.util.Constants;
import io.github.null2264.cobblegen.util.GeneratorType;
import io.github.null2264.cobblegen.util.Util;
import lombok.val;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableStatic;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.helpers.IPlatformFluidHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static io.github.null2264.cobblegen.CobbleGen.getCompat;
import static io.github.null2264.cobblegen.util.Util.identifierOf;

public class FluidInteractionCategory implements IRecipeCategory<FluidInteractionRecipeHolder>
{
    private final IDrawableStatic background;
    private final Long full;
    private final IDrawable icon;
    private final GeneratorType type;

    public FluidInteractionCategory(
            IGuiHelper guiHelper,
            IPlatformFluidHelper<?> fluidHelper,
            GeneratorType generatorType
    ) {
        background = guiHelper.createBlankDrawable(
                Constants.JEI_RECIPE_WIDTH,
                generatorType.equals(GeneratorType.STONE) ? Constants.JEI_RECIPE_HEIGHT_STONE
                                                          : Constants.JEI_RECIPE_HEIGHT
        );
        full = 10 * fluidHelper.bucketVolume();
        ItemStack iconStack = Items.AIR.getDefaultStack();
        switch (generatorType) {
            case COBBLE -> iconStack = Items.COBBLESTONE.getDefaultStack();
            case STONE -> iconStack = Items.STONE.getDefaultStack();
            case BASALT -> iconStack = Items.BASALT.getDefaultStack();
        }
        icon = guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK, iconStack);
        type = generatorType;
    }

    @NotNull
    @Override
    public RecipeType<FluidInteractionRecipeHolder> getRecipeType() {
        return new RecipeType<>(identifierOf(type), FluidInteractionRecipeHolder.class);
    }
    @NotNull
    @Override
    public Text getTitle() {
        return getCompat().translatableText("cobblegen.generators." + type.name().toLowerCase());
    }

    @NotNull
    @Override
    public IDrawable getBackground() {
        return background;
    }

    @NotNull
    @Override
    public IDrawable getIcon() {
        return icon;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, FluidInteractionRecipeHolder recipe, IFocusGroup focuses) {
        val output = recipe.getResult();
        val modifier = recipe.getModifier();

        val offset = Constants.SLOT_SIZE;
        var x = 0;
        var y = 0;
        val gap = 2;

        var isBasaltGen = type == GeneratorType.BASALT;
        var coldY = y;

        var lavaX = x + (2 * (offset + gap));

        var resultX = x + offset + gap;
        var resultY = y;
        var resultModY = y + offset + gap;

        if (type == GeneratorType.STONE) {
            lavaX = x + offset + gap;
            coldY = y + offset + gap;
            resultY = resultModY;
            resultModY = resultModY + offset + gap;
        }

        val coldBuilder = builder.addSlot(RecipeIngredientRole.INPUT, x, coldY);
        if (isBasaltGen) coldBuilder.addItemStack(Blocks.BLUE_ICE.asItem().getDefaultStack());
        else coldBuilder.addFluidStack(Fluids.WATER, full);
        builder.addSlot(RecipeIngredientRole.INPUT, lavaX, y).addFluidStack(Fluids.LAVA, full);
        builder.addSlot(RecipeIngredientRole.OUTPUT, resultX, resultY)
                .addItemStack(output.getBlock().asItem().getDefaultStack());
        if (modifier != null) builder.addSlot(RecipeIngredientRole.INPUT, resultX, resultModY)
                .addItemStack(modifier.asItem().getDefaultStack());
    }

    @Override
    public void draw(
            FluidInteractionRecipeHolder recipe,
            IRecipeSlotsView recipeSlotsView,
            MatrixStack stack,
            double mouseX,
            double mouseY
    ) {
        MinecraftClient minecraft = MinecraftClient.getInstance();
        var minY = recipe.getResult().minY;
        if (minY == null) minY = minecraft.world != null ? minecraft.world.getBottomY() : 0;
        var maxY = recipe.getResult().maxY;
        if (maxY == null) maxY = minecraft.world != null ? minecraft.world.getTopY() : 256;
        List<Text> texts = List.of(
                getCompat().translatableTextWithLiteral("cobblegen.info.weight", Text.of(recipe.getResult().weight.toString())),
                getCompat().translatableTextWithLiteral("cobblegen.info.minY", Text.of(minY.toString())),
                getCompat().translatableTextWithLiteral("cobblegen.info.maxY", Text.of(maxY.toString()))
        );
        TextRenderer textRenderer = minecraft.textRenderer;
        var y = 0;
        for (Text text : texts) {
            int width = textRenderer.getWidth(text);
            minecraft.textRenderer.draw(stack, text, getBackground().getWidth() - width, y, 0xFF808080);
            y += textRenderer.fontHeight;
        }
        // TODO: Add dimensions
    }

    @NotNull
    @Override
    public List<Text> getTooltipStrings(
            FluidInteractionRecipeHolder recipe, IRecipeSlotsView recipeSlotsView, double mouseX, double mouseY
    ) {
        // TODO: Add dimensions
        return List.of();
    }

    @SuppressWarnings("removal")
    @Deprecated
    @NotNull
    @Override
    public Identifier getUid() {
        return Util.identifierOf("fluid_interaction");
    }

    @SuppressWarnings("removal")
    @Deprecated
    @NotNull
    @Override
    public Class<? extends FluidInteractionRecipeHolder> getRecipeClass() {
        return FluidInteractionRecipeHolder.class;
    }
}