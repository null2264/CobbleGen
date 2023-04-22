package io.github.null2264.cobblegen.integration.emi;

import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import io.github.null2264.cobblegen.config.WeightedBlock;
import io.github.null2264.cobblegen.integration.FluidInteractionRecipeHolder;
import io.github.null2264.cobblegen.util.Constants;
import io.github.null2264.cobblegen.util.GeneratorType;
import io.github.null2264.cobblegen.util.Util;
import lombok.val;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.fluid.Fluids;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import static io.github.null2264.cobblegen.CobbleGen.getCompat;

public class FluidInteractionCategory extends FluidInteractionRecipeHolder implements EmiRecipe
{
    private final int initialHeight;

    public FluidInteractionCategory(
            WeightedBlock result, GeneratorType type, @Nullable Block modifier
    ) {
        super(result, type, modifier);
        initialHeight = type.equals(GeneratorType.STONE) ? Constants.JEI_RECIPE_HEIGHT_STONE
                                                                  : Constants.JEI_RECIPE_HEIGHT;
    }

    @Override
    public int getDisplayWidth() {
        return 150;
    }

    @Override
    public int getDisplayHeight() {
        return initialHeight
                + (2 * 9)  // Dimensions Title's gaps (top and bottom)
                + 20  // Dimension Icon's height
                + 9  // Another gap
                + 2;  // Last gap between border and dimension icon
    }

    @Override
    public EmiRecipeCategory getCategory() {
        return CGEMIPlugin.FLUID_INTERACTION.get(getType().name());
    }

    @Override
    public List<EmiIngredient> getInputs() {
        EmiStack lava = EmiStack.of(Fluids.LAVA, 81_000);
        EmiStack water = EmiStack.of(Fluids.WATER, 81_000);
        return List.of(
                lava.copy().setRemainder(lava),
                getType().equals(GeneratorType.BASALT) ? EmiStack.of(Blocks.BLUE_ICE) : water.copy().setRemainder(water),
                EmiStack.of(getModifier() != null ? getModifier() : Blocks.AIR)
        );
    }

    @Override
    public List<EmiStack> getOutputs() {
        return List.of(
                EmiStack.of(getResult().getBlock())
        );
    }

    @Override
    public void addWidgets(WidgetHolder widgets) {
        val offset = Constants.SLOT_SIZE;
        val gap = 2;
        val base = new Point(0, 0);

        val cold = (Point) base.clone();

        val lava = (Point) base.clone();
        lava.x += 2 * (offset + gap);

        val result = (Point) base.clone();
        result.x += offset + gap;
        val resultMod = (Point) result.clone();
        resultMod.y += offset + gap;

        if (getType() == GeneratorType.STONE) {
            lava.x -= offset + gap;
            cold.y += offset + gap;
            result.y = resultMod.y;
            resultMod.y += offset + gap;
        }

        widgets.addSlot(getInputs().get(0), lava.x, lava.y).catalyst(true);
        widgets.addSlot(getInputs().get(1), cold.x, cold.y).catalyst(true);
        widgets.addSlot(getOutputs().get(0), result.x, result.y).recipeContext(this);
        widgets.addSlot(getInputs().get(2), resultMod.x, resultMod.y).catalyst(true);

        // Additional Info
        MinecraftClient minecraft = MinecraftClient.getInstance();
        TextRenderer textRenderer = minecraft.textRenderer;
        var minY = getResult().minY;
        if (minY == null) minY = minecraft.world != null ? minecraft.world.getBottomY() : 0;
        var maxY = getResult().maxY;
        if (maxY == null) maxY = minecraft.world != null ? minecraft.world.getTopY() : 256;
        List<Text> texts = List.of(getCompat().translatableAppendingText("cobblegen.info.weight",
                                                                         Text.of(getResult().weight.toString())
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
            val labelPoint = new Point(getDisplayWidth() - textRenderer.getWidth(text), y);
            widgets.addText(text, labelPoint.x, labelPoint.y, 0xFFFFFFFF, true);
            y += 9;
        }

        // Dimensions
        Text text = getCompat().translatableText("cobblegen.info.dimensions");
        val dimensionTitlePoint = new Point((getDisplayWidth() / 2) - (textRenderer.getWidth(text) / 2), resultMod.y + offset + 9);
        widgets.addText(text, dimensionTitlePoint.x, dimensionTitlePoint.y, 0xFFFFFFFF, true);

        val dimensionBounds = (Point) base.clone();
        dimensionBounds.y = dimensionTitlePoint.y + (2 * 9);

        // Whitelisted Dimensions
        val whitelistBounds = (Point) dimensionBounds.clone();
        whitelistBounds.x += 18;

        val whitelist = new ArrayList<TooltipComponent>();
        whitelist.add(TooltipComponent.of(getCompat().toOrderedText(getCompat().translatableText("cobblegen.info.whitelistedDim"))));
        List<String> recipeWhitelist = getResult().dimensions;
        try {
            for (String biome : recipeWhitelist) {
                Identifier id = new Identifier(biome);
                whitelist.add(TooltipComponent.of(getCompat().toOrderedText(getCompat().text("- " + id))));
            }
        } catch (NullPointerException ignored) {
            whitelist.add(TooltipComponent.of(getCompat().toOrderedText(getCompat().appendingText("- ", getCompat().translatableText("cobblegen.dim.any")))));
        }
        widgets.addTexture(Constants.JEI_UI_COMPONENT, whitelistBounds.x, whitelistBounds.y, 15, 20, 0, 0).tooltip(
                (mouseX, mouseY) -> whitelist
        );

        // Blacklisted Dimensions
        val blacklistBounds = (Point) dimensionBounds.clone();
        blacklistBounds.x += getDisplayWidth() - 15 - 18;

        val blacklist = new ArrayList<TooltipComponent>();
        blacklist.add(TooltipComponent.of(getCompat().toOrderedText(getCompat().translatableText("cobblegen.info.blacklistedDim"))));
        List<String> recipeBlacklist = getResult().excludedDimensions;
        try {
            for (String biome : recipeBlacklist) {
                Identifier id = new Identifier(biome);
                blacklist.add(TooltipComponent.of(getCompat().toOrderedText(getCompat().text("- " + id))));
            }
        } catch (NullPointerException ignored) {
            blacklist.add(TooltipComponent.of(getCompat().toOrderedText(getCompat().appendingText("- ", getCompat().translatableText("cobblegen.dim.none")))));
        }
        widgets.addTexture(Constants.JEI_UI_COMPONENT, blacklistBounds.x, blacklistBounds.y, 15, 20, 15, 0).tooltip(
                (mouseX, mouseY) -> blacklist
        );
    }

    @Override
    public Identifier getId() {
        Identifier resultId = new Identifier(getResult().id);
        Identifier modifierId = Util.identifierOf("none");
        if (getModifier() != null) {
            modifierId = getModifier().getLootTableId();
        }
        return Util.identifierOf(CGEMIPlugin.ID_PREFIX + getType().name().toLowerCase() + "_" + resultId.getNamespace() + "-" + resultId.getPath() + "_" + modifierId.getNamespace() + "-" + modifierId.getPath());
    }
}