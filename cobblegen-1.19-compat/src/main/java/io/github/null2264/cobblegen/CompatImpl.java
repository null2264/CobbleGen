package io.github.null2264.cobblegen;

import io.github.null2264.cobblegen.util.Compat;
import net.minecraft.block.Block;
import net.minecraft.fluid.Fluid;
import net.minecraft.tag.TagKey;
import net.minecraft.text.MutableText;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.WorldAccess;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CompatImpl implements Compat
{
    @Override
    public MutableText translatableText(String string) {
        return Text.translatable(string);
    }

    @Override
    public MutableText translatableAppendingText(String string, Text... texts) {
        MutableText text = translatableText(string);
        for (Text appendText : texts) {
            text.append(appendText);
        }
        return text;
    }

    @Override
    public MutableText text(String string) {
        return Text.literal(string);
    }

    @Override
    public MutableText appendingText(String string, Text... texts) {
        MutableText text = text(string);
        for (Text appendText : texts) {
            text.append(appendText);
        }
        return text;
    }

    @Override
    public OrderedText toOrderedText(Text text) {
        return text.asOrderedText();
    }

    @Override
    public Block getBlock(Identifier id) {
        return Registry.BLOCK.get(id);
    }

    @Override
    public Identifier getBlockId(Block block) {
        return Registry.BLOCK.getId(block);
    }

    @Override
    public List<Identifier> getTaggedBlockIds(Identifier tagId) {
        TagKey<Block> blockTag = TagKey.of(Registry.BLOCK_KEY, tagId);

        ArrayList<Identifier> blockIds = new ArrayList<>();
        Registry.BLOCK.getEntryList(blockTag).ifPresent(t -> t.stream().forEach(taggedBlock -> {
            Optional<RegistryKey<Block>> key = taggedBlock.getKey();
            if (key.isPresent()) {
                RegistryKey<Block> actualKey = key.get();
                blockIds.add(actualKey.getValue());
            }
        }));
        return blockIds;
    }

    @Override
    public Identifier getFluidId(Fluid fluid) {
        return Registry.FLUID.getId(fluid);
    }

    @Override
    public String getDimension(WorldAccess world) {
        Identifier dim = world.getRegistryManager().get(Registry.DIMENSION_TYPE_KEY).getId(world.getDimension());
        return dim != null ? dim.toString() : "minecraft:overworld";
    }
}