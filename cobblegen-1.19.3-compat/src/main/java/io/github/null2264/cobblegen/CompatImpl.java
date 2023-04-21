package io.github.null2264.cobblegen;

import io.github.null2264.cobblegen.util.Compat;
import net.minecraft.block.Block;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CompatImpl extends Compat
{
    @Override
    public MutableText translatableText(String string) {
        return Text.translatable(string);
    }

    @Override
    public MutableText translatableAppendingText(String string, List<Text> texts) {
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
    public MutableText appendingText(String string, List<Text> texts) {
        MutableText text = text(string);
        for (Text appendText : texts) {
            text.append(appendText);
        }
        return text;
    }

    @Override
    public Block getBlock(Identifier id) {
        return Registries.BLOCK.get(id);
    }

    @Override
    public
    Identifier getBlockId(Block block) {
        return Registries.BLOCK.getId(block);
    }

    @Override
    public List<Identifier> getTaggedBlockIds(Identifier tagId) {
        TagKey<Block> blockTag = TagKey.of(RegistryKeys.BLOCK, tagId);

        ArrayList<Identifier> blockIds = new ArrayList<>();
        Registries.BLOCK.getEntryList(blockTag).ifPresent(t -> t.stream().forEach(taggedBlock -> {
            Optional<RegistryKey<Block>> key = taggedBlock.getKey();
            if (key.isPresent()) {
                RegistryKey<Block> actualKey = key.get();
                blockIds.add(actualKey.getValue());
            }
        }));
        return blockIds;
    }

    @Override
    public String getDimension(World world) {
        return world.getRegistryKey().getValue().toString();
    }
}