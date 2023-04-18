package io.github.null2264.cobblegen;

import io.github.null2264.cobblegen.util.Compat;
import net.minecraft.block.Block;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CompatImpl extends Compat
{
    @Override
    public Block getBlock(Identifier id) {
        return Registries.BLOCK.get(id);
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