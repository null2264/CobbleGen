//#if FABRIC>=1 && MC<=1.18.2 && MC>1.16.5 || MC>=1.19
package io.github.null2264.cobblegen.integration.viewer.emi;

import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import io.github.null2264.cobblegen.compat.LoaderCompat;
import io.github.null2264.cobblegen.compat.TextCompat;
import io.github.null2264.cobblegen.data.config.WeightedBlock;
import io.github.null2264.cobblegen.integration.viewer.FluidInteractionRecipeHolder;
import io.github.null2264.cobblegen.util.Constants;
import io.github.null2264.cobblegen.util.GeneratorType;
import io.github.null2264.cobblegen.util.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.material.Fluid;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import static io.github.null2264.cobblegen.util.Util.identifierOf;

public class FluidInteractionRecipe extends FluidInteractionRecipeHolder implements EmiRecipe
{
    private final int initialHeight;

    public FluidInteractionRecipe(
            Fluid sourceFluid,
            Fluid neighbourFluid,
            Block neighbourBlock,
            WeightedBlock result,
            GeneratorType type,
            Block modifier
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
        EmiStack source = EmiStack.of(getSourceFluid(), LoaderCompat.isForge() ? 1_000 : 81_000);
        EmiStack neighbour = EmiStack.of(getNeighbourFluid(), LoaderCompat.isForge() ? 1_000 : 81_000);
        return List.of(
                source.copy().setRemainder(source),
                getType().equals(GeneratorType.BASALT) ? EmiStack.of(getNeighbourBlock())
                        : neighbour.copy().setRemainder(neighbour),
                EmiStack.of(getModifier())
        );
    }

    @Override
    public List<EmiStack> getOutputs() {
        return List.of(EmiStack.of(getResult().getBlock()));
    }

    @Override
    public void addWidgets(WidgetHolder widgets) {
        final int offset = Constants.SLOT_SIZE;
        final int gap = 2;
        final Point base = new Point(0, 0);

        final Point cold = (Point) base.clone();

        final Point lava = (Point) base.clone();
        lava.x += 2 * (offset + gap);

        final Point result = (Point) base.clone();
        result.x += offset + gap;
        final Point resultMod = (Point) result.clone();
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
            final Point labelPoint = new Point(getDisplayWidth() - textRenderer.width(text), y);
            widgets.addText(text, labelPoint.x, labelPoint.y, 0xFFFFFFFF, true);
            y += 9;
        }

        // Dimensions
        Component text = TextCompat.translatable("cobblegen.info.dimensions");
        final Point dimensionTitlePoint = new Point(
                (getDisplayWidth() / 2) - (textRenderer.width(text) / 2),
                resultMod.y + offset + 9
        );
        widgets.addText(text, dimensionTitlePoint.x, dimensionTitlePoint.y, 0xFFFFFFFF, true);

        final Point dimensionBounds = (Point) base.clone();
        dimensionBounds.y = dimensionTitlePoint.y + (2 * 9);

        // Whitelisted Dimensions
        final Point whitelistBounds = (Point) dimensionBounds.clone();
        whitelistBounds.x += 18;

        final ArrayList<ClientTooltipComponent> whitelist = new ArrayList<>();
        whitelist.add(ClientTooltipComponent.create(TextCompat.translatable("cobblegen.info.whitelistedDim")
                .getVisualOrderText()));
        List<String> recipeWhitelist = getResult().dimensions;
        try {
            for (String dim : recipeWhitelist) {
                ResourceLocation id;
                try {
                    id = ResourceLocation.tryParse(dim);
                } catch (Exception e) {
                    continue;
                }
                whitelist.add(ClientTooltipComponent.create(TextCompat.literal("- " + id).getVisualOrderText()));
            }
        } catch (NullPointerException ignored) {
            whitelist.add(
                    ClientTooltipComponent.create(TextCompat.literal("- ")
                                    .append(TextCompat.translatable("cobblegen.dim.any"))
                                    .getVisualOrderText()
            ));
        }
        widgets.addTexture(Constants.JEI_UI_COMPONENT.toMC(), whitelistBounds.x, whitelistBounds.y, 15, 20, 0, 0)
                .tooltip((mouseX, mouseY) -> whitelist);

        // Blacklisted Dimensions
        final Point blacklistBounds = (Point) dimensionBounds.clone();
        blacklistBounds.x += getDisplayWidth() - 15 - 18;

        final ArrayList<ClientTooltipComponent> blacklist = new ArrayList<>();
        blacklist.add(ClientTooltipComponent.create(TextCompat.translatable(
                "cobblegen.info.blacklistedDim").getVisualOrderText()));
        List<String> recipeBlacklist = getResult().excludedDimensions;
        try {
            for (String dim : recipeBlacklist) {
                ResourceLocation id;
                try {
                    id = ResourceLocation.tryParse(dim);
                } catch (Exception e) {
                    continue;
                }
                blacklist.add(ClientTooltipComponent.create(TextCompat.literal("- " + id).getVisualOrderText()));
            }
        } catch (NullPointerException ignored) {
            blacklist.add(
                    ClientTooltipComponent.create(TextCompat.literal("- ")
                                    .append(TextCompat.translatable("cobblegen.dim.none"))
                                    .getVisualOrderText()
            ));
        }
        widgets.addTexture(Constants.JEI_UI_COMPONENT.toMC(), blacklistBounds.x, blacklistBounds.y, 15, 20, 15, 0)
                .tooltip((mouseX, mouseY) -> blacklist);
    }

    @Override
    public ResourceLocation getId() {
        ResourceLocation resultId;
        try {
            resultId = ResourceLocation.tryParse(getResult().id);
        } catch (Exception e) {
            resultId = identifierOf(getResult().id);
        }
        ResourceLocation source = Util.getFluidId(getSourceFluid());
        ResourceLocation neighbour;
        if (getNeighbourBlock().equals(Blocks.AIR))
            neighbour = Util.getFluidId(getNeighbourFluid());
        else
            neighbour = Util.getBlockId(getNeighbourBlock());
        ResourceLocation modifierId = identifierOf("none");
        if (!(getModifier().equals(Blocks.AIR)))
            modifierId = Util.getBlockId(getModifier());
        return identifierOf(CGEMIPlugin.ID_PREFIX + getType().name()
                .toLowerCase() + "-" + source.toDebugFileName() + "-" + resultId.toDebugFileName() + "-" + neighbour.toDebugFileName() + "-" + modifierId.toDebugFileName());
    }
}
//#endif