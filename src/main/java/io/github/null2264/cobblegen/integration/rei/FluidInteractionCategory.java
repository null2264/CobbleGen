package io.github.null2264.cobblegen.integration.rei;

import io.github.null2264.cobblegen.data.Constants;
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
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import static io.github.null2264.cobblegen.CobbleGen.getCompat;

public class FluidInteractionCategory implements DisplayCategory<FluidInteractionRecipeHolderDisplay>
{
    public static String ID_PREFIX = "fluid_interaction_";
    private final Renderer icon;
    private final GeneratorType type;
    private final int initialHeight;

    public FluidInteractionCategory(GeneratorType generatorType) {
        initialHeight = generatorType.equals(GeneratorType.STONE) ? Constants.JEI_RECIPE_HEIGHT_STONE
                                                                  : Constants.JEI_RECIPE_HEIGHT;
        ItemStack iconStack = Items.AIR.getDefaultStack();
        switch (generatorType) {
            case COBBLE -> iconStack = Items.COBBLESTONE.getDefaultStack();
            case STONE -> iconStack = Items.STONE.getDefaultStack();
            case BASALT -> iconStack = Items.BASALT.getDefaultStack();
        }
        icon = EntryStacks.of(iconStack);
        type = generatorType;
    }

    public static CategoryIdentifier<? extends FluidInteractionRecipeHolderDisplay> generateIdentifier(GeneratorType type) {
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

    @NotNull
    @Override
    public Text getTitle() {
        return getCompat().translatableText("cobblegen.generators." + type.name().toLowerCase());
    }

    @Override
    public Renderer getIcon() {
        return icon;
    }

    @Override
    public List<Widget> setupDisplay(FluidInteractionRecipeHolderDisplay display, Rectangle bounds) {
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
        val modifier = display.getInputEntries().get(2).get(0);
        if (modifier != null)
            widgets.add(Widgets.createSlot(resultMod).entry(modifier).markInput().disableBackground());

        // Additional Info
        MinecraftClient minecraft = MinecraftClient.getInstance();
        var minY = display.getResult().minY;
        if (minY == null) minY = minecraft.world != null ? minecraft.world.getBottomY() : 0;
        var maxY = display.getResult().maxY;
        if (maxY == null) maxY = minecraft.world != null ? minecraft.world.getTopY() : 256;
        List<Text> texts = List.of(getCompat().translatableAppendingText("cobblegen.info.weight",
                                                                         Text.of(display.getResult().weight.toString())
                                   ),
                                   getCompat().translatableAppendingText("cobblegen.info.minY",
                                                                         Text.of(minY.toString())
                                   ),
                                   getCompat().translatableAppendingText("cobblegen.info.maxY",
                                                                         Text.of(maxY.toString())
                                   )
        );
        var y = base.y;
        for (Text text : texts) {
            val labelPoint = new Point(bounds.x + getDisplayWidth(display) - gapAgainstBound, y);
            val label = Widgets.createLabel(labelPoint, text).rightAligned().noShadow().color(0xFF404040, 0xFFBBBBBB);
            widgets.add(label);
            y += 9;
        }

        // Dimensions
        Text text = getCompat().translatableText("cobblegen.info.dimensions");
        val dimensionTitlePoint = new Point(bounds.getCenterX(), resultMod.y + offset + 9);
        widgets.add(Widgets.createLabel(dimensionTitlePoint, text).centered().noShadow().color(0xFF404040, 0xFFBBBBBB));

        val dimensionBounds = base.clone();
        dimensionBounds.resize(15, 20);
        dimensionBounds.y = dimensionTitlePoint.y + (2 * 9);

        // Whitelisted Dimensions
        val whitelistBounds = dimensionBounds.clone();
        whitelistBounds.x += 18;
        val whitelistIcon = Widgets.createTexturedWidget(Constants.JEI_UI_COMPONENT, whitelistBounds, 0F, 0F, 256, 256);

        val whitelist = new ArrayList<Text>();
        whitelist.add(getCompat().translatableText("cobblegen.info.whitelistedDim"));
        List<String> recipeWhitelist = display.getResult().dimensions;
        try {
            for (String biome : recipeWhitelist) {
                Identifier id = new Identifier(biome);
                whitelist.add(getCompat().text("- " + id));
            }
        } catch (NullPointerException ignored) {
            whitelist.add(getCompat().appendingText("- ", getCompat().translatableText("cobblegen.dim.any")));
        }
        widgets.add(Widgets.withTooltip(Widgets.withBounds(whitelistIcon, whitelistBounds), whitelist));

        // Blacklisted Dimensions
        val blacklistBounds = dimensionBounds.clone();
        blacklistBounds.x += bounds.width - 15 - 18 - (2 * gapAgainstBound);
        val blacklistIcon = Widgets.createTexturedWidget(Constants.JEI_UI_COMPONENT,
                                                         blacklistBounds,
                                                         15F,
                                                         0F,
                                                         256,
                                                         256
        );

        val blacklist = new ArrayList<Text>();
        blacklist.add(getCompat().translatableText("cobblegen.info.blacklistedDim"));
        List<String> recipeBlacklist = display.getResult().excludedDimensions;
        try {
            for (String biome : recipeBlacklist) {
                Identifier id = new Identifier(biome);
                blacklist.add(getCompat().text("- " + id));
            }
        } catch (NullPointerException ignored) {
            blacklist.add(getCompat().appendingText("- ", getCompat().translatableText("cobblegen.dim.none")));
        }
        widgets.add(Widgets.withTooltip(Widgets.withBounds(blacklistIcon, blacklistBounds), blacklist));
        return widgets;
    }

    @Override
    public CategoryIdentifier<? extends FluidInteractionRecipeHolderDisplay> getCategoryIdentifier() {
        return generateIdentifier(type);
    }
}