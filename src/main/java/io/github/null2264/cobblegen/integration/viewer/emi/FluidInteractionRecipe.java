package io.github.null2264.cobblegen.integration.viewer.emi;

import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import io.github.null2264.cobblegen.compat.TextCompat;
import io.github.null2264.cobblegen.config.WeightedBlock;
import io.github.null2264.cobblegen.integration.viewer.FluidInteractionRecipeHolder;
import io.github.null2264.cobblegen.util.Constants;
import io.github.null2264.cobblegen.util.GeneratorType;
import io.github.null2264.cobblegen.util.Util;
import lombok.val;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class FluidInteractionRecipe extends FluidInteractionRecipeHolder implements EmiRecipe
{
    private final int initialHeight;

    public FluidInteractionRecipe(
            Fluid sourceFluid,
            @Nullable Fluid neighbourFluid,
            @Nullable Block neighbourBlock,
            WeightedBlock result,
            GeneratorType type,
            @Nullable Block modifier
    ) {
        super(sourceFluid, neighbourFluid, neighbourBlock, result, type, modifier);
        initialHeight =
                type.equals(GeneratorType.STONE) ? Constants.JEI_RECIPE_HEIGHT_STONE : Constants.JEI_RECIPE_HEIGHT;
    }

    @Override
    public int getDisplayWidth() {
        return 150;
    }

    @Override
    public int getDisplayHeight() {
        return initialHeight + (2 * 9)  // Dimensions Title's gaps (top and bottom)
                + 20  // Dimension Icon's height
                + 9  // Another gap
                + 2;  // Last gap between border and dimension icon
    }

    @Override
    public EmiRecipeCategory getCategory() {
        return CGEMIPlugin.FLUID_INTERACTION_CATEGORIES.get(getType().name());
    }

    @Override
    public List<EmiIngredient> getInputs() {
        EmiStack source = EmiStack.of(getSourceFluid(), 81_000);
        EmiStack neighbour = EmiStack.of(getNeighbourFluid() != null ? getNeighbourFluid() : Fluids.EMPTY, 81_000);
        return List.of(
                source.copy().setRemainder(source),
                getType().equals(GeneratorType.BASALT) ? EmiStack.of(getNeighbourBlock() != null ? getNeighbourBlock() : Blocks.AIR)
                        : neighbour.copy().setRemainder(neighbour),
                EmiStack.of(getModifier() != null ? getModifier() : Blocks.AIR)
        );
    }

    @Override
    public List<EmiStack> getOutputs() {
        return List.of(EmiStack.of(getResult().getBlock()));
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
        Minecraft minecraft = Minecraft.getInstance();
        Font textRenderer = minecraft.font;
        var minY = getResult().minY;
        if (minY == null) minY = minecraft.level != null ? minecraft.level.getMinBuildHeight() : 0;
        var maxY = getResult().maxY;
        if (maxY == null) maxY = minecraft.level != null ? minecraft.level.getMaxBuildHeight() : 256;
        List<Component> texts = List.of(
                TextCompat.translatable("cobblegen.info.weight")
                        .append(Component.nullToEmpty(getResult().weight.toString())),
                TextCompat.translatable("cobblegen.info.minY")
                        .append(Component.nullToEmpty(minY.toString())),
                TextCompat.translatable("cobblegen.info.maxY")
                        .append(Component.nullToEmpty(maxY.toString()))
        );
        var y = base.y;
        for (Component text : texts) {
            val labelPoint = new Point(getDisplayWidth() - textRenderer.width(text), y);
            widgets.addText(text, labelPoint.x, labelPoint.y, 0xFFFFFFFF, true);
            y += 9;
        }

        // Dimensions
        Component text = TextCompat.translatable("cobblegen.info.dimensions");
        val dimensionTitlePoint = new Point(
                (getDisplayWidth() / 2) - (textRenderer.width(text) / 2),
                resultMod.y + offset + 9
        );
        widgets.addText(text, dimensionTitlePoint.x, dimensionTitlePoint.y, 0xFFFFFFFF, true);

        val dimensionBounds = (Point) base.clone();
        dimensionBounds.y = dimensionTitlePoint.y + (2 * 9);

        // Whitelisted Dimensions
        val whitelistBounds = (Point) dimensionBounds.clone();
        whitelistBounds.x += 18;

        val whitelist = new ArrayList<ClientTooltipComponent>();
        whitelist.add(ClientTooltipComponent.create(TextCompat.translatable("cobblegen.info.whitelistedDim")
                .getVisualOrderText()));
        List<String> recipeWhitelist = getResult().dimensions;
        try {
            for (String dim : recipeWhitelist) {
                ResourceLocation id = new ResourceLocation(dim);
                whitelist.add(ClientTooltipComponent.create(TextCompat.literal("- " + id).getVisualOrderText()));
            }
        } catch (NullPointerException ignored) {
            whitelist.add(
                    ClientTooltipComponent.create(TextCompat.literal("- ")
                                    .append(TextCompat.translatable("cobblegen.dim.any"))
                                    .getVisualOrderText()
            ));
        }
        widgets.addTexture(Constants.JEI_UI_COMPONENT, whitelistBounds.x, whitelistBounds.y, 15, 20, 0, 0)
                .tooltip((mouseX, mouseY) -> whitelist);

        // Blacklisted Dimensions
        val blacklistBounds = (Point) dimensionBounds.clone();
        blacklistBounds.x += getDisplayWidth() - 15 - 18;

        val blacklist = new ArrayList<ClientTooltipComponent>();
        blacklist.add(ClientTooltipComponent.create(TextCompat.translatable(
                "cobblegen.info.blacklistedDim").getVisualOrderText()));
        List<String> recipeBlacklist = getResult().excludedDimensions;
        try {
            for (String dim : recipeBlacklist) {
                ResourceLocation id = new ResourceLocation(dim);
                blacklist.add(ClientTooltipComponent.create(TextCompat.literal("- " + id).getVisualOrderText()));
            }
        } catch (NullPointerException ignored) {
            blacklist.add(
                    ClientTooltipComponent.create(TextCompat.literal("- ")
                                    .append(TextCompat.translatable("cobblegen.dim.none"))
                                    .getVisualOrderText()
            ));
        }
        widgets.addTexture(Constants.JEI_UI_COMPONENT, blacklistBounds.x, blacklistBounds.y, 15, 20, 15, 0)
                .tooltip((mouseX, mouseY) -> blacklist);
    }

    @Override
    public ResourceLocation getId() {
        ResourceLocation resultId = new ResourceLocation(getResult().id);
        ResourceLocation source = Util.getFluidId(getSourceFluid());
        ResourceLocation neighbour;
        if (getNeighbourBlock() != null)
            neighbour = Util.getBlockId(getNeighbourBlock());
        else
            neighbour = Util.getFluidId(getNeighbourFluid());
        ResourceLocation modifierId = Util.identifierOf("none");
        if (getModifier() != null)
            modifierId = Util.getBlockId(getModifier());
        return Util.identifierOf(CGEMIPlugin.ID_PREFIX + getType().name()
                .toLowerCase() + "-" + source.toDebugFileName() + "-" + resultId.toDebugFileName() + "-" + neighbour.toDebugFileName() + "-" + modifierId.toDebugFileName());
    }
}