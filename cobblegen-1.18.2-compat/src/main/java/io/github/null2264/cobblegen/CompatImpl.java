package io.github.null2264.cobblegen;

import io.github.null2264.cobblegen.util.Compat;
import net.minecraft.block.Block;
import net.minecraft.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CompatImpl extends Compat
{
    @Override
    public Block getBlock(Identifier id) {
        return Registry.BLOCK.get(id);
    }

    @Override
    public
    Identifier getBlockId(Block block) {
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
    public String getDimension(World world) {
        return world.getRegistryKey().getValue().toString();
    }
}