//#if FABRIC>=1 && MC<=1.18.2 && MC>1.16.5 || MC>=1.19
package io.github.null2264.cobblegen.integration.viewer.emi;

import dev.emi.emi.api.EmiPlugin;
import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.recipe.EmiWorldInteractionRecipe;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.SlotWidget;
import io.github.null2264.cobblegen.CobbleGen;
import io.github.null2264.cobblegen.compat.LoaderCompat;
import io.github.null2264.cobblegen.compat.TextCompat;
import io.github.null2264.cobblegen.config.WeightedBlock;
import io.github.null2264.cobblegen.util.Util;
import lombok.val;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.material.Fluids;

import java.util.*;
import java.util.function.Function;

import static io.github.null2264.cobblegen.CobbleGen.FLUID_INTERACTION;

//#if MC>=11900
//$$ @dev.emi.emi.api.EmiEntrypoint
//#endif
public class CGEMIPlugin implements EmiPlugin
{
    public static final String ID_PREFIX = "fluid_interaction_";
    public static final Map<String, EmiRecipeCategory> FLUID_INTERACTION_CATEGORIES = Map.of(
            "COBBLE",
            new EmiRecipeCategory(
                    Util.identifierOf(
                            ID_PREFIX + "cobble"),
                    EmiStack.of(
                            Blocks.COBBLESTONE)
            ),
            "STONE",
            new EmiRecipeCategory(
                    Util.identifierOf(
                            ID_PREFIX + "stone"),
                    EmiStack.of(
                            Blocks.STONE)
            ),
            "BASALT",
            new EmiRecipeCategory(
                    Util.identifierOf(
                            ID_PREFIX + "basalt"),
                    EmiStack.of(
                            Blocks.BASALT)
            )
    );

    enum InputPosition {
        LEFT, RIGHT
    }

    private void input(
            EmiWorldInteractionRecipe.Builder recipe,
            EmiIngredient input,
            boolean catalyst,
            InputPosition position
    ) {
        if (position.equals(InputPosition.RIGHT))
            recipe.rightInput(input, catalyst);
        else
            recipe.leftInput(input);
    }

    private void input(
            EmiWorldInteractionRecipe.Builder recipe,
            EmiIngredient input,
            boolean catalyst,
            Function<SlotWidget, SlotWidget> mutator,
            InputPosition position
    ) {
        if (position.equals(InputPosition.RIGHT))
            recipe.rightInput(input, catalyst, mutator);
        else
            recipe.leftInput(input, mutator);
    }

    @Override
    public void register(EmiRegistry registry) {
        if (!CobbleGen.META_CONFIG.enableRecipeViewer)
            return;

        if (!CobbleGen.META_CONFIG.mergeEMIRecipeCategory) {
            FLUID_INTERACTION_CATEGORIES.forEach((ignored, category) -> registry.addCategory(category));
            FLUID_INTERACTION.getGenerators().forEach((fluid, generators) -> generators.forEach(generator -> generator.getOutput().forEach(
                    (modifierId, blocks) -> {
                        Block modifier = null;
                        if (!Objects.equals(modifierId, "*"))
                            modifier = Util.getBlock(new ResourceLocation(modifierId));
                        for (WeightedBlock block : blocks)
                            registry.addRecipe(
                                    new FluidInteractionRecipe(
                                            fluid,
                                            Util.notNullOr(generator.getFluid(), Fluids.EMPTY),
                                            Util.notNullOr(generator.getBlock(), Blocks.AIR),
                                            block,
                                            generator.getType(),
                                            Util.notNullOr(modifier, Blocks.AIR)
                                    )
                            );
                    })));
        } else {
            Minecraft minecraft = Minecraft.getInstance();
            FLUID_INTERACTION.getGenerators().forEach((fluid, generators) -> generators.forEach(generator -> generator.getOutput().forEach(
                    (modifierRawId, blocks) -> {
                        EmiStack trigger = EmiStack.of(fluid, LoaderCompat.isForge() ? 1_000 : 81_000);

                        ResourceLocation modifierId = new ResourceLocation("none");
                        Optional<Block> modifier = Optional.empty();
                        if (!Objects.equals(modifierRawId, "*")) {
                            modifierId = new ResourceLocation(modifierRawId);
                            modifier = Optional.of(Util.getBlock(modifierId));
                        }

                        for (WeightedBlock block : blocks) {
                            ResourceLocation resultId = new ResourceLocation(block.id);
                            EmiStack output = EmiStack.of(Util.getBlock(resultId));

                            ResourceLocation source = Util.getFluidId(fluid);
                            EmiStack neighbour;
                            ResourceLocation neighbourId;
                            if (generator.getFluid() == null) {
                                neighbourId = Util.getBlockId(generator.getBlock());
                                neighbour = EmiStack.of(Objects.requireNonNull(generator.getBlock()));
                            } else {
                                neighbourId = Util.getFluidId(generator.getFluid());
                                neighbour = EmiStack.of(Objects.requireNonNull(generator.getFluid()), LoaderCompat.isForge() ? 1_000 : 81_000);
                            }

                            if (CobbleGen.META_CONFIG.emi.removeOverlaps) {
                                registry.removeRecipes(r ->
                                        new HashSet<>(r.getInputs()).containsAll(List.of(neighbour, trigger)) && r.getOutputs().contains(output) && r.getId().toString().startsWith("emi")
                                );
                            }

                            val id = Util.identifierOf(CGEMIPlugin.ID_PREFIX + generator.getType().name()
                                    .toLowerCase() + "-" + source.toDebugFileName() + "-" + resultId.toDebugFileName() + "-" + neighbourId.toDebugFileName() + "-" + modifierId.toDebugFileName());

                            val recipe = EmiWorldInteractionRecipe.builder()
                                    .id(id);

                            input(
                                    recipe,
                                    trigger.copy().setRemainder(trigger),
                                    false,
                                    CobbleGen.META_CONFIG.emi.invertInput ? InputPosition.RIGHT : InputPosition.LEFT
                            );

                            EmiStack neighbourRemainder = neighbour.isEmpty() ? neighbour : neighbour.copy().setRemainder(neighbour);

                            if (modifier.isPresent()) {
                                input(
                                        recipe,
                                        EmiStack.of(modifier.get()),
                                        false,
                                        s -> s.appendTooltip(TextCompat.translatable("tooltip.emi.fluid_interaction.basalt.soul_soil").withStyle(ChatFormatting.GREEN)),
                                        CobbleGen.META_CONFIG.emi.invertInput ? InputPosition.LEFT : InputPosition.RIGHT
                                );
                                input(
                                        recipe,
                                        neighbourRemainder,
                                        false,
                                        s -> generator.getBlock() != null ? s.appendTooltip(TextCompat.translatable("tooltip.emi.fluid_interaction.basalt.blue_ice").withStyle(ChatFormatting.GREEN)) : s,
                                        CobbleGen.META_CONFIG.emi.invertInput ? InputPosition.LEFT : InputPosition.RIGHT
                                );
                            } else {
                                input(
                                        recipe,
                                        neighbourRemainder,
                                        false,
                                        CobbleGen.META_CONFIG.emi.invertInput ? InputPosition.LEFT : InputPosition.RIGHT
                                );
                            }

                            recipe.output(output,
                                    s -> {
                                        if (!CobbleGen.META_CONFIG.emi.addTooltip) return s;

                                        var minY = block.minY;
                                        if (minY == null)
                                            minY = minecraft.level != null ? minecraft.level.getMinBuildHeight() : 0;
                                        var maxY = block.maxY;
                                        if (maxY == null)
                                            maxY = minecraft.level != null ? minecraft.level.getMaxBuildHeight() : 256;

                                        s.appendTooltip(TextCompat.translatable("cobblegen.info.weight")
                                                .append(Component.nullToEmpty(block.weight.toString())));
                                        s.appendTooltip(TextCompat.translatable("cobblegen.info.minY")
                                                .append(Component.nullToEmpty(minY.toString())));
                                        s.appendTooltip(TextCompat.translatable("cobblegen.info.maxY")
                                                .append(Component.nullToEmpty(maxY.toString())));

                                        s.appendTooltip(TextCompat.translatable("cobblegen.info.blacklistedDim").withStyle(ChatFormatting.GREEN));
                                        List<String> recipeBlacklist = block.excludedDimensions;
                                        try {
                                            for (String dim : recipeBlacklist) {
                                                ResourceLocation dimId = new ResourceLocation(dim);
                                                s.appendTooltip(TextCompat.literal("- " + dimId));
                                            }
                                        } catch (NullPointerException ignored) {
                                            s.appendTooltip(TextCompat.literal("- ")
                                                    .append(TextCompat.translatable("cobblegen.dim.none")));
                                        }

                                        s.appendTooltip(TextCompat.translatable("cobblegen.info.whitelistedDim").withStyle(ChatFormatting.GREEN));
                                        List<String> recipeWhitelist = block.dimensions;
                                        try {
                                            for (String dim : recipeWhitelist) {
                                                ResourceLocation dimId = new ResourceLocation(dim);
                                                s.appendTooltip(TextCompat.literal("- " + dimId));
                                            }
                                        } catch (NullPointerException ignored) {
                                            s.appendTooltip(TextCompat.literal("- ")
                                                    .append(TextCompat.translatable("cobblegen.dim.any")));
                                        }
                                        return s;
                                    }
                            );
                            registry.addRecipe(recipe.build());
                        }
                    }
            )));
        }
    }
}
//#endif