package io.github.null2264.cobblegen.util;

import io.github.null2264.cobblegen.config.WeightedBlock;
import lombok.val;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import static io.github.null2264.cobblegen.CobbleGen.getCompat;

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
        if (blockIds.size() == 1) return blockIds.get(0).id;

        ArrayList<WeightedBlock> filteredBlockIds = new ArrayList<>();
        AtomicReference<Double> totalWeight = new AtomicReference<>(0.0);

        for (WeightedBlock block : blockIds) {
            if (block.dimensions != null && !block.dimensions.contains(dim)) continue;

            if (block.excludedDimensions != null && block.excludedDimensions.contains(dim)) continue;

            if (block.maxY != null && block.maxY <= yLevel) continue;

            if (block.minY != null && block.minY >= yLevel) continue;

            if (block.id.startsWith("#")) {
                List<Identifier> taggedBlocks = getCompat().getTaggedBlockIds(new Identifier(block.id.substring(1)));
                for (Identifier taggedBlock : taggedBlocks) {
                    filteredBlockIds.add(new WeightedBlock(taggedBlock.toString(), block.weight));
                    totalWeight.updateAndGet(v -> v + block.weight);
                }
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

        if (filteredBlockIds.isEmpty()) return null;
        return filteredBlockIds.get(idx).id;
    }

    public static List<WeightedBlock> getCustomReplacement(World world, BlockPos pos, GeneratorType type) {
        List<WeightedBlock> replacements = null;
        Block blockBelow = world.getBlockState(pos.down()).getBlock();
        val config = Util.configFromType(type);
        Map<String, List<WeightedBlock>> customGen = config.getRight();
        List<WeightedBlock> fallback = config.getLeft();

        if (customGen != null) replacements = customGen.get(getCompat().getBlockId(blockBelow).toString());

        if (type == GeneratorType.BASALT) return blockBelow == Blocks.SOUL_SOIL ? fallback : replacements;

        if (replacements == null || replacements.isEmpty()) {
            replacements = fallback;
        }

        return replacements;
    }

    public @Nullable BlockState getReplacement() {
        String replacementId = null;
        if (expectedBlocks != null && !expectedBlocks.isEmpty())
            replacementId = randomizeBlockId(expectedBlocks, getCompat().getDimension(world), pos.getY());

        if (replacementId == null) return null;

        return getCompat().getBlock(new Identifier(replacementId)).getDefaultState();
    }

    public void tryReplace(Args args) {
        val replacement = getReplacement();
        if (replacement != null) args.set(1, replacement);
    }
}