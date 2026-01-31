#if MC>11605
package io.github.null2264.cobblegen.integration.viewer.jei;

#if MC>=12100
import mezz.jei.api.gui.builder.ITooltipBuilder;
#endif

import io.github.null2264.cobblegen.data.CGIdentifier;
import io.github.null2264.cobblegen.compat.GraphicsCompat;
import io.github.null2264.cobblegen.compat.TextCompat;
import io.github.null2264.cobblegen.data.config.WeightedBlock;
import io.github.null2264.cobblegen.integration.viewer.FluidInteractionRecipeHolder;
import io.github.null2264.cobblegen.util.Constants;
import io.github.null2264.cobblegen.util.GeneratorType;
import io.github.null2264.cobblegen.util.Util;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.IRecipeSlotBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.helpers.IPlatformFluidHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class FluidInteractionCategory implements IRecipeCategory<FluidInteractionRecipeHolder>
{
    private final IDrawable background;
    private final Long full;
    private final IDrawable icon;
    private final GeneratorType type;
    private final int initialHeight;
    private int height;
    private final IDrawable whitelistIcon;
    private final IDrawable blacklistIcon;
    private int dimensionIconsY = 0;

    public FluidInteractionCategory(
            IGuiHelper guiHelper, IPlatformFluidHelper<?> fluidHelper, GeneratorType generatorType
    ) {
        initialHeight = generatorType.equals(GeneratorType.STONE) ? Constants.JEI_RECIPE_HEIGHT_STONE
                : Constants.JEI_RECIPE_HEIGHT;
        height =
            initialHeight
                // Dimensions Title's gaps (top and bottom)
                + (2 * 9)
                // Dimension Icon's height
                + 20
                // Another gap
                + 9;
        background = guiHelper.createBlankDrawable(Constants.JEI_RECIPE_WIDTH, height);
        full = 10 * fluidHelper.bucketVolume();
        ItemStack iconStack = Items.AIR.getDefaultInstance();
        switch (generatorType) {
            case COBBLE -> iconStack = Items.COBBLESTONE.getDefaultInstance();
            case STONE -> iconStack = Items.STONE.getDefaultInstance();
            case BASALT -> iconStack = Items.BASALT.getDefaultInstance();
        }
        icon = guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK, iconStack);
        type = generatorType;
        whitelistIcon = guiHelper.drawableBuilder(Constants.JEI_UI_COMPONENT.toMC(), 0, 0, 15, 20).build();
        blacklistIcon = guiHelper.drawableBuilder(Constants.JEI_UI_COMPONENT.toMC(), 15, 0, 15, 20).build();
    }

    @NotNull
    @Override
    public
    mezz.jei.api.recipe
        #if MC<12111
        .RecipeType
        #else
        .types.IRecipeType
        #endif
        <FluidInteractionRecipeHolder>
    getRecipeType() {
        return
            #if MC<12111
            new mezz.jei.api.recipe.RecipeType<>(
            #else
            mezz.jei.api.recipe.types.IRecipeType.create(
            #endif
                CGIdentifier.of(type).toMC(), FluidInteractionRecipeHolder.class);
    }

    @NotNull
    @Override
    public Component getTitle() {
        return TextCompat.translatable("cobblegen.generators." + type.name().toLowerCase());
    }

    #if MC>=12111
    @Override
    public int getWidth() {
        return Constants.JEI_RECIPE_WIDTH;
    }

    @Override
    public int getHeight() {
        return height;
    }
    #else
    #if MC<12100
    @NotNull
    #else
    @org.jetbrains.annotations.Nullable
    #endif
    @Override
    public IDrawable getBackground() {
        return background;
    }
    #endif

    @NotNull
    @Override
    public IDrawable getIcon() {
        return icon;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, FluidInteractionRecipeHolder recipe, IFocusGroup focuses) {
        final WeightedBlock output = recipe.getResult();
        final Block modifier = recipe.getModifier();
        final Fluid source = recipe.getSourceFluid();
        final Fluid neighbourFluid = recipe.getNeighbourFluid();
        final Block neighbourBlock = recipe.getNeighbourBlock();

        final int offset = Constants.SLOT_SIZE;
        var x = 0;
        var y = 0;
        final int gap = 2;

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

        final IRecipeSlotBuilder coldBuilder = builder.addSlot(RecipeIngredientRole.INPUT, x, coldY);
        if (type.equals(GeneratorType.BASALT)) coldBuilder
            #if MC<12111
            .addItemStack(
            #else
            .add(
            #endif
                neighbourBlock.asItem().getDefaultInstance());
        else coldBuilder
            #if MC<12111
            .addFluidStack(
            #else
            .add(
            #endif
                neighbourFluid, full);
        builder.addSlot(RecipeIngredientRole.INPUT, lavaX, y)
            #if MC<12111
            .addFluidStack(
            #else
            .add(
            #endif
                source, full);
        builder.addSlot(RecipeIngredientRole.OUTPUT, resultX, resultY)
            #if MC<12111
            .addItemStack(
            #else
            .add(
            #endif
                output.getBlock().asItem().getDefaultInstance());
        builder.addSlot(RecipeIngredientRole.INPUT, resultX, resultModY)
            #if MC<12111
            .addItemStack(
            #else
            .add(
            #endif
                modifier.asItem().getDefaultInstance());
    }

    @Override
    public void draw(
            FluidInteractionRecipeHolder recipe,
            IRecipeSlotsView recipeSlotsView,
            #if MC<12000
            com.mojang.blaze3d.vertex.PoseStack graphicsTarget,
            #else
            net.minecraft.client.gui.GuiGraphics graphicsTarget,
            #endif
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
            GraphicsCompat.drawString(graphicsTarget, text, background.getWidth() - width, y, 0xFF808080);
            y += font.lineHeight;
        }
        Component text = TextCompat.translatable("cobblegen.info.dimensions");
        var deepestY = initialHeight + 9;
        GraphicsCompat.drawString(graphicsTarget, text, (int) (((float) background.getWidth() / 2) - ((float) font.width(text) / 2)), deepestY, 0xFF808080);
        deepestY = deepestY + font.lineHeight + 9;
        dimensionIconsY = deepestY;
        whitelistIcon.draw(
                graphicsTarget,
                18,
                deepestY
        );
        blacklistIcon.draw(
                graphicsTarget,
                background.getWidth() - 15 - 18,
                deepestY
        );
    }

    #if MC<12100
    @NotNull
    @Override
    public List<Component> getTooltipStrings(FluidInteractionRecipeHolder recipe, IRecipeSlotsView recipeSlotsView, double mouseX, double mouseY) {
        return getTooltip(recipe, recipeSlotsView, mouseX, mouseY);
    }
    #else
    @Override
    public void getTooltip(ITooltipBuilder tooltip, FluidInteractionRecipeHolder recipe, IRecipeSlotsView recipeSlotsView, double mouseX, double mouseY) {
        tooltip.addAll(getTooltip(recipe, recipeSlotsView, mouseX, mouseY));
    }
    #endif

    @NotNull
    public List<Component> getTooltip(
        FluidInteractionRecipeHolder recipe, IRecipeSlotsView recipeSlotsView, double mouseX, double mouseY
    ) {
        if ((mouseX > 18 && mouseX < 18 + 15) && (mouseY > dimensionIconsY && mouseY < dimensionIconsY + 20)) {
            ArrayList<Component> dimList = new ArrayList<>();
            dimList.add(TextCompat.translatable("cobblegen.info.whitelistedDim"));

            List<String> recipeDimList = recipe.getResult().dimensions;
            if (recipeDimList == null) {
                dimList.add(TextCompat.literal("- ").append(TextCompat.translatable("cobblegen.dim.any")));
            } else {
                for (String dim : recipeDimList) {
                    CGIdentifier id;
                    try {
                        id = CGIdentifier.of(dim);
                    } catch (Exception e) {
                        continue;
                    }
                    dimList.add(TextCompat.literal("- " + id));
                }
            }
            return dimList;
        }

        final int aetherX = background.getWidth() - 18;
        if ((mouseX > aetherX - 15 && mouseX < aetherX) && (mouseY > dimensionIconsY && mouseY < dimensionIconsY + 20)) {
            ArrayList<Component> dimList = new ArrayList<>();
            dimList.add(TextCompat.translatable("cobblegen.info.blacklistedDim"));

            List<String> recipeDimList = recipe.getResult().excludedDimensions;
            if (recipeDimList == null) {
                dimList.add(TextCompat.literal("- ").append(TextCompat.translatable("cobblegen.dim.none")));
            } else {
                for (String dim : recipeDimList) {
                    CGIdentifier id;
                    try {
                        id = CGIdentifier.of(dim);
                    } catch (Exception e) {
                        continue;
                    }
                    dimList.add(TextCompat.literal("- " + id));
                }
            }
            return dimList;
        }
        return List.of();
    }

    #if MC<11900
    @SuppressWarnings("removal")
    @Deprecated
    @NotNull
    public
    net.minecraft.resources.
    #if MC>=12111
    Identifier
    #else
    ResourceLocation
    #endif
    getUid() {
        return CGIdentifier.of("fluid_interaction").toMC();
    }

    @SuppressWarnings("removal")
    @Deprecated
    @NotNull
    public Class<? extends FluidInteractionRecipeHolder> getRecipeClass() {
        return FluidInteractionRecipeHolder.class;
    }
    #endif
}
#endif
