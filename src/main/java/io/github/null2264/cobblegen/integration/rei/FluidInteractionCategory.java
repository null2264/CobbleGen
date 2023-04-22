package io.github.null2264.cobblegen.integration.rei;

import io.github.null2264.cobblegen.integration.FluidInteractionRecipeHolder;
import io.github.null2264.cobblegen.util.Constants;
import io.github.null2264.cobblegen.util.GeneratorType;
import io.github.null2264.cobblegen.util.Util;
import lombok.val;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.gui.Renderer;
import me.shedaniel.rei.api.client.gui.widgets.Widget;
import me.shedaniel.rei.api.client.gui.widgets.Widgets;
import me.shedaniel.rei.api.client.registry.display.DisplayCategory;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.util.EntryStacks;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import static io.github.null2264.cobblegen.CobbleGen.getCompat;
import static io.github.null2264.cobblegen.util.Util.identifierOf;

public class FluidInteractionCategory implements DisplayCategory<FluidInteractionRecipeHolderDisplay>
{
    public static String ID_PREFIX = "fluid_interaction_";
    //private final Long full;
    private final Renderer icon;
    private final GeneratorType type;
    private final int initialHeight;
    //private int dimensionIconsY = 0;

    public FluidInteractionCategory(GeneratorType generatorType) {
        initialHeight = generatorType.equals(GeneratorType.STONE) ? Constants.JEI_RECIPE_HEIGHT_STONE
                                                                  : Constants.JEI_RECIPE_HEIGHT;
        //full = 10 * fluidHelper.bucketVolume();
        ItemStack iconStack = Items.AIR.getDefaultStack();
        switch (generatorType) {
            case COBBLE -> iconStack = Items.COBBLESTONE.getDefaultStack();
            case STONE -> iconStack = Items.STONE.getDefaultStack();
            case BASALT -> iconStack = Items.BASALT.getDefaultStack();
        }
        icon = EntryStacks.of(iconStack);
        type = generatorType;
        //whitelistIcon = guiHelper.drawableBuilder(Constants.JEI_UI_COMPONENT, 0, 0, 15, 20).build();
        //blacklistIcon = guiHelper.drawableBuilder(Constants.JEI_UI_COMPONENT, 15, 0, 15, 20).build();
    }

    @Override
    public int getDisplayWidth(FluidInteractionRecipeHolderDisplay display) {
        return Constants.JEI_RECIPE_WIDTH;
    }

    @Override
    public int getDisplayHeight() {
        return initialHeight + (2 * 9)  // Dimensions Title's gaps (top and bottom)
                + 20  // Dimension Icon's height
                + 9;  // Another gap
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

        var cold = new Rectangle();
        cold.resize(Constants.SLOT_SIZE, Constants.SLOT_SIZE);

        var lava = new Rectangle();
        lava.resize(Constants.SLOT_SIZE, Constants.SLOT_SIZE);
        lava.move(bounds.x + (2 * (offset + gap)), bounds.y);

        var result = new Rectangle();
        result.resize(Constants.SLOT_SIZE, Constants.SLOT_SIZE);
        result.move(bounds.x + offset + gap, bounds.y);
        var resultMod = new Rectangle();
        resultMod.resize(Constants.SLOT_SIZE, Constants.SLOT_SIZE);
        resultMod.move(result.x, result.y + offset + gap);

        if (type == GeneratorType.STONE) {
            lava.move(bounds.x + offset + gap, bounds.y);
            cold.move(cold.x, bounds.y + offset + gap);
            result.move(result.x, resultMod.y);
            resultMod.move(result.x, resultMod.y + offset + gap);
        }

        List<Widget> widgets = new ArrayList<>();
        widgets.add(Widgets.createRecipeBase(bounds));

        widgets.add(Widgets.createSlot(lava).entries(display.getInputEntries().get(0)).markInput());
        widgets.add(Widgets.createSlot(cold).entries(display.getInputEntries().get(1)).markInput());
        widgets.add(Widgets.createSlot(result).entries(display.getOutputEntries().get(0)).markOutput());
        val modifier = display.getInputEntries().get(2).get(0);
        if (modifier != null)
            widgets.add(Widgets.createSlot(resultMod).entry(modifier).markInput());
        return widgets;
    }

    /*
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
        List<Text> texts = List.of(getCompat().translatableAppendingText(
                                           "cobblegen.info.weight",
                                           Text.of(recipe.getResult().weight.toString())
                                   ),
                                   getCompat().translatableAppendingText(
                                           "cobblegen.info.minY",
                                           Text.of(minY.toString())
                                   ),
                                   getCompat().translatableAppendingText(
                                           "cobblegen.info.maxY",
                                           Text.of(maxY.toString())
                                   )
        );
        TextRenderer textRenderer = minecraft.textRenderer;
        var y = 0;
        for (Text text : texts) {
            int width = textRenderer.getWidth(text);
            minecraft.textRenderer.draw(stack, text, getDisplayWidth() - width, y, 0xFF808080);
            y += textRenderer.fontHeight;
        }
        Text text = getCompat().translatableText("cobblegen.info.dimensions");
        var deepestY = initialHeight + 9;
        minecraft.textRenderer.draw(stack,
                                    text,
                                    ((float) getBackground().getWidth() / 2) - ((float) textRenderer.getWidth(text) / 2),
                                    deepestY,
                                    0xFF808080
        );
        deepestY = deepestY + textRenderer.fontHeight + 9;
        dimensionIconsY = deepestY;
        whitelistIcon.draw(stack, 18, deepestY);
        blacklistIcon.draw(stack, getBackground().getWidth() - 15 - 18, deepestY);
    }

    @NotNull
    @Override
    public List<Text> getTooltipStrings(
            FluidInteractionRecipeHolder recipe, IRecipeSlotsView recipeSlotsView, double mouseX, double mouseY
    ) {
        if ((mouseX > 18 && mouseX < 18 + 15) && (mouseY > dimensionIconsY && mouseY < dimensionIconsY + 20)) {
            ArrayList<Text> biomeList = new ArrayList<>();
            biomeList.add(getCompat().translatableText("cobblegen.info.whitelistedDim"));

            List<String> recipeBiomeList = recipe.getResult().dimensions;
            try {
                for (String biome : recipeBiomeList) {
                    Identifier id = new Identifier(biome);
                    biomeList.add(getCompat().text("- " + id));
                }
            } catch (NullPointerException ignored) {
                biomeList.add(getCompat().appendingText(
                        "- ",
                        getCompat().translatableText("cobblegen.dim.any")
                ));
            }
            return biomeList;
        }

        val aetherX = getBackground().getWidth() - 18;
        if ((mouseX > aetherX - 15 && mouseX < aetherX) && (mouseY > dimensionIconsY && mouseY < dimensionIconsY + 20)) {
            ArrayList<Text> biomeList = new ArrayList<>();
            biomeList.add(getCompat().translatableText("cobblegen.info.blacklistedDim"));

            List<String> recipeBiomeList = recipe.getResult().excludedDimensions;
            try {
                for (String biome : recipeBiomeList) {
                    Identifier id = new Identifier(biome);
                    biomeList.add(getCompat().text("- " + id));
                }
            } catch (NullPointerException ignored) {
                biomeList.add(getCompat().appendingText(
                        "- ",
                        getCompat().translatableText("cobblegen.dim.none")
                ));
            }
            return biomeList;
        }
        return List.of();
    }
     */

    public static CategoryIdentifier<? extends FluidInteractionRecipeHolderDisplay> generateIdentifier(GeneratorType type) {
        return CategoryIdentifier.of(Util.identifierOf(ID_PREFIX + type.name().toLowerCase()));
    }

    @Override
    public CategoryIdentifier<? extends FluidInteractionRecipeHolderDisplay> getCategoryIdentifier() {
        return generateIdentifier(type);
    }
}