package io.github.null2264.cobblegen.integration.viewer.jei;

import io.github.null2264.cobblegen.compat.GraphicsCompat;
import io.github.null2264.cobblegen.compat.TextCompat;
import io.github.null2264.cobblegen.integration.viewer.FluidInteractionRecipeHolder;
import io.github.null2264.cobblegen.util.Constants;
import io.github.null2264.cobblegen.util.GeneratorType;
import io.github.null2264.cobblegen.util.Util;
import lombok.val;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.helpers.IPlatformFluidHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import static io.github.null2264.cobblegen.util.Util.identifierOf;

public class FluidInteractionCategory implements IRecipeCategory<FluidInteractionRecipeHolder>
{
    private final IDrawable background;
    private final Long full;
    private final IDrawable icon;
    private final GeneratorType type;
    private final int initialHeight;
    private final IDrawable whitelistIcon;
    private final IDrawable blacklistIcon;
    private int dimensionIconsY = 0;

    public FluidInteractionCategory(
            IGuiHelper guiHelper, IPlatformFluidHelper<?> fluidHelper, GeneratorType generatorType
    ) {
        initialHeight = generatorType.equals(GeneratorType.STONE) ? Constants.JEI_RECIPE_HEIGHT_STONE
                : Constants.JEI_RECIPE_HEIGHT;
        background = guiHelper.createBlankDrawable(
                Constants.JEI_RECIPE_WIDTH,
                initialHeight + (2 * 9) // Dimensions Title's gaps (top and bottom)
                        + 20 // Dimension Icon's height
                        + 9
                // Another gap
        );
        full = 10 * fluidHelper.bucketVolume();
        ItemStack iconStack = Items.AIR.getDefaultInstance();
        switch (generatorType) {
            case COBBLE -> iconStack = Items.COBBLESTONE.getDefaultInstance();
            case STONE -> iconStack = Items.STONE.getDefaultInstance();
            case BASALT -> iconStack = Items.BASALT.getDefaultInstance();
        }
        icon = guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK, iconStack);
        type = generatorType;
        whitelistIcon = guiHelper.drawableBuilder(Constants.JEI_UI_COMPONENT, 0, 0, 15, 20).build();
        blacklistIcon = guiHelper.drawableBuilder(Constants.JEI_UI_COMPONENT, 15, 0, 15, 20).build();
    }

    @NotNull
    @Override
    public RecipeType<FluidInteractionRecipeHolder> getRecipeType() {
        return new RecipeType<>(identifierOf(type), FluidInteractionRecipeHolder.class);
    }

    @NotNull
    @Override
    public Component getTitle() {
        return TextCompat.translatable("cobblegen.generators." + type.name().toLowerCase());
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
        val source = recipe.getSourceFluid();
        val neighbourFluid = recipe.getNeighbourFluid();
        val neighbourBlock = recipe.getNeighbourBlock();

        val offset = Constants.SLOT_SIZE;
        var x = 0;
        var y = 0;
        val gap = 2;

        var coldY = y;

        var lavaX = x + (2 * (offset + gap));

        var resultX = x + offset + gap;
        var resultY = y;
        var resultModY = y + offset + gap;

        if (type.equals(GeneratorType.STONE)) {
            lavaX = x + offset + gap;
            coldY = y + offset + gap;
            resultY = resultModY;
            resultModY = resultModY + offset + gap;
        }

        val coldBuilder = builder.addSlot(RecipeIngredientRole.INPUT, x, coldY);
        if (type.equals(GeneratorType.BASALT)) coldBuilder.addItemStack(neighbourBlock.asItem().getDefaultInstance());
        else coldBuilder.addFluidStack(neighbourFluid, full);
        builder.addSlot(RecipeIngredientRole.INPUT, lavaX, y).addFluidStack(source, full);
        builder.addSlot(RecipeIngredientRole.OUTPUT, resultX, resultY)
                .addItemStack(output.getBlock().asItem().getDefaultInstance());
        builder.addSlot(RecipeIngredientRole.INPUT, resultX, resultModY)
                .addItemStack(modifier.asItem().getDefaultInstance());
    }

    @Override
    public void draw(
            FluidInteractionRecipeHolder recipe,
            IRecipeSlotsView recipeSlotsView,
            //#if MC<12000
            com.mojang.blaze3d.vertex.PoseStack graphicsTarget,
            //#else
            //$$ net.minecraft.client.gui.GuiGraphics graphicsTarget,
            //#endif
            double mouseX,
            double mouseY
    ) {
        Minecraft minecraft = Minecraft.getInstance();
        Font font = minecraft.font;
        var minY = recipe.getResult().minY;
        if (minY == null) minY = minecraft.level != null ? minecraft.level.getMinBuildHeight() : 0;
        var maxY = recipe.getResult().maxY;
        if (maxY == null) maxY = minecraft.level != null ? minecraft.level.getMaxBuildHeight() : 256;
        List<Component> texts = List.of(
                TextCompat.translatable("cobblegen.info.weight")
                        .append(Component.nullToEmpty(recipe.getResult().weight.toString())),
                TextCompat.translatable("cobblegen.info.minY")
                        .append(Component.nullToEmpty(minY.toString())),
                TextCompat.translatable("cobblegen.info.maxY")
                        .append(Component.nullToEmpty(maxY.toString()))
        );

        var y = 0;
        for (Component text : texts) {
            int width = font.width(text);
            GraphicsCompat.drawString(graphicsTarget, text, getBackground().getWidth() - width, y, 0xFF808080);
            y += font.lineHeight;
        }
        Component text = TextCompat.translatable("cobblegen.info.dimensions");
        var deepestY = initialHeight + 9;
        GraphicsCompat.drawString(graphicsTarget, text, (int) (((float) getBackground().getWidth() / 2) - ((float) font.width(text) / 2)), deepestY, 0xFF808080);
        deepestY = deepestY + font.lineHeight + 9;
        dimensionIconsY = deepestY;
        whitelistIcon.draw(
                //#if MC<12000
                graphicsTarget,
                //#else
                //$$ graphics,
                //#endif
                18,
                deepestY
        );
        blacklistIcon.draw(
                //#if MC<12000
                graphicsTarget,
                //#else
                //$$ graphics,
                //#endif
                getBackground().getWidth() - 15 - 18,
                deepestY
        );
    }

    @NotNull
    @Override
    public List<Component> getTooltipStrings(
            FluidInteractionRecipeHolder recipe, IRecipeSlotsView recipeSlotsView, double mouseX, double mouseY
    ) {
        if ((mouseX > 18 && mouseX < 18 + 15) && (mouseY > dimensionIconsY && mouseY < dimensionIconsY + 20)) {
            ArrayList<Component> biomeList = new ArrayList<>();
            biomeList.add(TextCompat.translatable("cobblegen.info.whitelistedDim"));

            List<String> recipeDimList = recipe.getResult().dimensions;
            try {
                for (String dim : recipeDimList) {
                    ResourceLocation id = new ResourceLocation(dim);
                    biomeList.add(TextCompat.literal("- " + id));
                }
            } catch (NullPointerException ignored) {
                biomeList.add(TextCompat.literal("- ").append(TextCompat.translatable("cobblegen.dim.any")));
            }
            return biomeList;
        }

        val aetherX = getBackground().getWidth() - 18;
        if ((mouseX > aetherX - 15 && mouseX < aetherX) && (mouseY > dimensionIconsY && mouseY < dimensionIconsY + 20)) {
            ArrayList<Component> biomeList = new ArrayList<>();
            biomeList.add(TextCompat.translatable("cobblegen.info.blacklistedDim"));

            List<String> recipeDimList = recipe.getResult().excludedDimensions;
            try {
                for (String dim : recipeDimList) {
                    ResourceLocation id = new ResourceLocation(dim);
                    biomeList.add(TextCompat.literal("- " + id));
                }
            } catch (NullPointerException ignored) {
                biomeList.add(TextCompat.literal("- ").append(TextCompat.translatable("cobblegen.dim.none")));
            }
            return biomeList;
        }
        return List.of();
    }

    //#if MC<11900
    @SuppressWarnings("removal")
    @Deprecated
    @NotNull
    public ResourceLocation getUid() {
        return Util.identifierOf("fluid_interaction");
    }

    @SuppressWarnings("removal")
    @Deprecated
    @NotNull
    public Class<? extends FluidInteractionRecipeHolder> getRecipeClass() {
        return FluidInteractionRecipeHolder.class;
    }
    //#endif
}