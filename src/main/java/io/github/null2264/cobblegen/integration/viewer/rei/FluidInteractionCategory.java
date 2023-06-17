package io.github.null2264.cobblegen.integration.viewer.rei;

import io.github.null2264.cobblegen.compat.TextCompat;
import io.github.null2264.cobblegen.util.Constants;
import io.github.null2264.cobblegen.util.GeneratorType;
import io.github.null2264.cobblegen.util.Util;
import lombok.val;
import me.shedaniel.math.Point;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.gui.Renderer;
import me.shedaniel.rei.api.client.gui.widgets.Widget;
import me.shedaniel.rei.api.client.gui.widgets.Widgets;
import me.shedaniel.rei.api.client.registry.display.DisplayCategory;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.util.EntryStacks;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.ArrayList;
import java.util.List;

public class FluidInteractionCategory implements DisplayCategory<FluidInteractionRecipe>
{
    public static String ID_PREFIX = "fluid_interaction_";
    private final Renderer icon;
    private final GeneratorType type;
    private final int initialHeight;

    public FluidInteractionCategory(GeneratorType generatorType) {
        initialHeight = generatorType.equals(GeneratorType.STONE) ? Constants.JEI_RECIPE_HEIGHT_STONE
                : Constants.JEI_RECIPE_HEIGHT;
        ItemStack iconStack = Items.AIR.getDefaultInstance();
        switch (generatorType) {
            case COBBLE -> iconStack = Items.COBBLESTONE.getDefaultInstance();
            case STONE -> iconStack = Items.STONE.getDefaultInstance();
            case BASALT -> iconStack = Items.BASALT.getDefaultInstance();
        }
        icon = EntryStacks.of(iconStack);
        type = generatorType;
    }

    public static CategoryIdentifier<? extends FluidInteractionRecipe> generateIdentifier(GeneratorType type) {
        return CategoryIdentifier.of(Util.identifierOf(ID_PREFIX + type.name().toLowerCase()));
    }

    @Override
    public int getDisplayHeight() {
        return initialHeight + (2 * 3)  // Gap against display's top border
                + (2 * 9)  // Dimensions Title's gaps (top and bottom)
                + 20  // Dimension Icon's height
                + 9  // Another gap
                + (2 * 3) + 2;  // Gap against display's bottom border
    }

    @Override
    public Component getTitle() {
        return TextCompat.translatable("cobblegen.generators." + type.name().toLowerCase());
    }

    @Override
    public Renderer getIcon() {
        return icon;
    }

    @Override
    public List<Widget> setupDisplay(FluidInteractionRecipe display, Rectangle bounds) {
        val offset = Constants.SLOT_SIZE;
        val gap = 2;
        val gapAgainstBound = gap * 3;
        val base = bounds.clone();
        base.resize(Constants.SLOT_SIZE, Constants.SLOT_SIZE);
        base.translate(gapAgainstBound, gapAgainstBound);

        var cold = base.clone();

        var lava = base.clone();
        lava.x += 2 * (offset + gap);

        var result = base.clone();
        result.x += offset + gap;
        var resultMod = result.clone();
        resultMod.y += offset + gap;

        if (type == GeneratorType.STONE) {
            lava.x -= offset + gap;
            cold.y += offset + gap;
            result.y = resultMod.y;
            resultMod.y += offset + gap;
        }

        List<Widget> widgets = new ArrayList<>();
        widgets.add(Widgets.createRecipeBase(bounds));
        widgets.add(Widgets.createSlot(lava).entries(display.getInputEntries().get(0)).markInput().disableBackground());
        widgets.add(Widgets.createSlot(cold).entries(display.getInputEntries().get(1)).markInput().disableBackground());
        widgets.add(Widgets.createSlot(result)
                            .entries(display.getOutputEntries().get(0))
                            .markOutput()
                            .disableBackground());
        widgets.add(Widgets.createSlot(resultMod).entry(display.getInputEntries().get(2).get(0)).markInput().disableBackground());

        // Additional Info
        Minecraft minecraft = Minecraft.getInstance();
        var minY = display.getResult().minY;
        if (minY == null) minY = minecraft.level != null ? minecraft.level.getMinBuildHeight() : 0;
        var maxY = display.getResult().maxY;
        if (maxY == null) maxY = minecraft.level != null ? minecraft.level.getMaxBuildHeight() : 256;
        List<Component> texts = List.of(
                TextCompat.translatable("cobblegen.info.weight")
                        .append(Component.nullToEmpty(display.getResult().weight.toString())),
                TextCompat.translatable("cobblegen.info.minY")
                        .append(Component.nullToEmpty(minY.toString())),
                TextCompat.translatable("cobblegen.info.maxY")
                        .append(Component.nullToEmpty(maxY.toString()))
        );

        var y = base.y;
        for (Component text : texts) {
            val labelPoint = new Point(bounds.x + getDisplayWidth(display) - gapAgainstBound, y);
            val label = Widgets.createLabel(labelPoint, text).rightAligned().noShadow().color(0xFF404040, 0xFFBBBBBB);
            widgets.add(label);
            y += 9;
        }

        // Dimensions
        Component text = TextCompat.translatable("cobblegen.info.dimensions");
        val dimensionTitlePoint = new Point(bounds.getCenterX(), resultMod.y + offset + 9);
        widgets.add(Widgets.createLabel(dimensionTitlePoint, text).centered().noShadow().color(0xFF404040, 0xFFBBBBBB));

        val dimensionBounds = base.clone();
        dimensionBounds.resize(15, 20);
        dimensionBounds.y = dimensionTitlePoint.y + (2 * 9);

        // Whitelisted Dimensions
        val whitelistBounds = dimensionBounds.clone();
        whitelistBounds.x += 18;
        val whitelistIcon = Widgets.createTexturedWidget(Constants.JEI_UI_COMPONENT, whitelistBounds, 0F, 0F, 256, 256);

        val whitelist = new ArrayList<Component>();
        whitelist.add(TextCompat.translatable("cobblegen.info.whitelistedDim"));
        List<String> recipeWhitelist = display.getResult().dimensions;
        try {
            for (String dim : recipeWhitelist) {
                ResourceLocation id = new ResourceLocation(dim);
                whitelist.add(TextCompat.literal("- " + id));
            }
        } catch (NullPointerException ignored) {
            whitelist.add(TextCompat.literal("- ").append(TextCompat.translatable("cobblegen.dim.any")));
        }
        widgets.add(Widgets.withTooltip(Widgets.withBounds(whitelistIcon, whitelistBounds), whitelist));

        // Blacklisted Dimensions
        val blacklistBounds = dimensionBounds.clone();
        blacklistBounds.x += bounds.width - 15 - 18 - (2 * gapAgainstBound);
        val blacklistIcon = Widgets.createTexturedWidget(
                Constants.JEI_UI_COMPONENT,
                blacklistBounds,
                15F,
                0F,
                256,
                256
        );

        val blacklist = new ArrayList<Component>();
        blacklist.add(TextCompat.translatable("cobblegen.info.blacklistedDim"));
        List<String> recipeBlacklist = display.getResult().excludedDimensions;
        try {
            for (String dim : recipeBlacklist) {
                ResourceLocation id = new ResourceLocation(dim);
                blacklist.add(TextCompat.literal("- " + id));
            }
        } catch (NullPointerException ignored) {
            blacklist.add(TextCompat.literal("- ").append(TextCompat.translatable("cobblegen.dim.none")));
        }
        widgets.add(Widgets.withTooltip(Widgets.withBounds(blacklistIcon, blacklistBounds), blacklist));
        return widgets;
    }

    @Override
    public CategoryIdentifier<? extends FluidInteractionRecipe> getCategoryIdentifier() {
        return generateIdentifier(type);
    }
}