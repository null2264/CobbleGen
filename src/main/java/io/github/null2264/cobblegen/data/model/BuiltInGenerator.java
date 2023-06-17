package io.github.null2264.cobblegen.data.model;

import io.github.null2264.cobblegen.config.WeightedBlock;
import io.github.null2264.cobblegen.util.Util;
import lombok.val;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.ApiStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

@ApiStatus.Internal
public interface BuiltInGenerator extends Generator
{
    // https://stackoverflow.com/a/6737362
    private String randomizeBlockId(Block key, String dim, Integer yLevel) {
        val blockIds = getOutput().getOrDefault(
                Util.getBlockId(key).toString(),
                getOutput().getOrDefault("*", List.of())
        );

        if (blockIds.isEmpty()) return null;

        if (blockIds.size() == 1) return blockIds.get(0).id;

        ArrayList<WeightedBlock> filteredBlockIds = new ArrayList<>();
        AtomicReference<Double> totalWeight = new AtomicReference<>(0.0);

        for (WeightedBlock block : blockIds) {
            if (block.dimensions != null && !block.dimensions.contains(dim)) continue;

            if (block.excludedDimensions != null && block.excludedDimensions.contains(dim)) continue;

            if (block.maxY != null && block.maxY <= yLevel) continue;

            if (block.minY != null && block.minY >= yLevel) continue;

            if (block.id.startsWith("#")) {
                List<ResourceLocation> taggedBlocks = Util.getTaggedBlockIds(new ResourceLocation(block.id.substring(1)));
                for (ResourceLocation taggedBlock : taggedBlocks) {
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

    default Optional<BlockState> getBlockCandidate(LevelAccessor level, BlockPos pos) {
        val replacementId = randomizeBlockId(
                level.getBlockState(pos.below()).getBlock(),
                Util.getDimension(level),
                pos.getY()
        );

        if (replacementId == null) return Optional.empty();

        return Optional.of(Util.getBlock(new ResourceLocation(replacementId)).defaultBlockState());
    }
}