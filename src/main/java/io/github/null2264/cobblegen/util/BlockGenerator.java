package io.github.null2264.cobblegen.util;

import io.github.null2264.cobblegen.config.WeightedBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

import static io.github.null2264.cobblegen.config.ConfigHelper.CONFIG;

public class BlockGenerator
{
    private final List<WeightedBlock> expectedBlocks;
    private final World world;
    private final BlockPos pos;

    public BlockGenerator(World world, BlockPos pos, GeneratorType type) {
        this.expectedBlocks = getCustomReplacement(world, pos, type);
        this.world = world;
        this.pos = pos;
    }

    // https://stackoverflow.com/a/6737362
    public static String randomizeBlockId(List<WeightedBlock> blockIds, String dim, Integer yLevel) {
        if (blockIds.size() == 1)
            return blockIds.get(0).id;

        ArrayList<WeightedBlock> filteredBlockIds = new ArrayList<>();
        AtomicReference<Double> totalWeight = new AtomicReference<>(0.0);

        for (WeightedBlock block : blockIds) {
            if (block.dimensions != null && !block.dimensions.contains(dim))
                continue;

            if (block.excludedDimensions != null && block.excludedDimensions.contains(dim))
                continue;

            if (block.maxY != null && block.maxY <= yLevel)
                continue;

            if (block.minY != null && block.minY >= yLevel)
                continue;

            if (block.id.startsWith("#")) {
                TagKey<Block> blockTag = TagKey.of(Registry.BLOCK_KEY, new Identifier(block.id.substring(1)));
                Registry.BLOCK.getEntryList(blockTag).ifPresent(t -> t.stream().forEach(taggedBlock -> {
                    Optional<RegistryKey<Block>> key = taggedBlock.getKey();
                    if (key.isPresent()) {
                        RegistryKey<Block> actualKey = key.get();
                        filteredBlockIds.add(
                                new WeightedBlock(
                                        actualKey.getValue().toString(),
                                        block.weight
                                )
                        );
                        totalWeight.updateAndGet(v -> v + block.weight);
                    }
                }));
            } else {
                filteredBlockIds.add(block);
                totalWeight.updateAndGet(v -> v + block.weight);
            }
        }

        int idx = 0;
        for (double r = Math.random() * totalWeight.get(); idx < filteredBlockIds.size() - 1; ++idx) {
            r -= filteredBlockIds.get(idx).weight;
            if (r <= 0.0) break;
        }

        if (filteredBlockIds.isEmpty())
            return null;
        return filteredBlockIds.get(idx).id;
    }

    public static List<WeightedBlock> getCustomReplacement(World world, BlockPos pos, GeneratorType type) {
        List<WeightedBlock> replacements = null;
        Block blockBelow = world.getBlockState(pos.down()).getBlock();
        Map<String, List<WeightedBlock>> customGen = Map.of();
        List<WeightedBlock> fallback = List.of();
        switch (type) {
            case COBBLE -> {
                customGen = CONFIG.customGen.cobbleGen;
                fallback = CONFIG.cobbleGen;
            }
            case STONE -> {
                customGen = CONFIG.customGen.stoneGen;
                fallback = CONFIG.stoneGen;
            }
            case BASALT -> {
                customGen = CONFIG.customGen.basaltGen;
                fallback = CONFIG.basaltGen;
            }
        }

        if (customGen != null)
            replacements = customGen.get(Registry.BLOCK.getId(blockBelow).toString());

        if (type == GeneratorType.BASALT)
            return blockBelow == Blocks.SOUL_SOIL ? fallback : replacements;

        if (replacements == null || replacements.isEmpty()) {
            replacements = fallback;
        }

        return replacements;
    }

    public @Nullable BlockState getReplacement() {
        String replacementId = null;
        if (expectedBlocks != null && !expectedBlocks.isEmpty())
            replacementId = randomizeBlockId(
                    expectedBlocks,
                    world.getRegistryKey().getValue().toString(),
                    pos.getY()
            );

        if (replacementId == null)
            return null;

        return Registry.BLOCK.get(new Identifier(replacementId)).getDefaultState();
    }

    public List<WeightedBlock> getExpectedBlocks() {
        return expectedBlocks;
    }
}