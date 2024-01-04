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
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

@ApiStatus.Internal
public interface BuiltInGenerator extends Generator
{
    // https://stackoverflow.com/a/6737362
    //#if MC<=1.16.5
    //$$ @ApiStatus.Internal
    //$$ default String
    //#else
    private String
    //#endif
    randomizeBlockId(Block key, String dim, Integer yLevel, Map<String, List<WeightedBlock>> candidates) {
        val blockIds = candidates.getOrDefault(
                Util.getBlockId(key).toString(),
                candidates.getOrDefault("*", List.of())
        );

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

        if (filteredBlockIds.isEmpty()) return null;

        if (filteredBlockIds.size() == 1) return filteredBlockIds.get(0).id;

        int idx = 0;
        for (double r = Math.random() * totalWeight.get(); idx < filteredBlockIds.size() - 1; ++idx) {
            r -= filteredBlockIds.get(idx).weight;
            if (r <= 0.0) break;
        }

        return filteredBlockIds.get(idx).id;
    }

    default Optional<BlockState> getBlockCandidate(LevelAccessor level, BlockPos pos, Map<String, List<WeightedBlock>> candidates) {
        return getBlockCandidate(level, pos, candidates, null);
    }

    default Optional<BlockState> getBlockCandidate(LevelAccessor level, BlockPos pos, Map<String, List<WeightedBlock>> candidates, Block defaultBlock) {
        val replacementId = randomizeBlockId(
                level.getBlockState(pos.below()).getBlock(),
                Util.getDimension(level),
                pos.getY(),
                candidates
        );

        if (replacementId == null) {
            if (defaultBlock != null)
                return Optional.of(defaultBlock.defaultBlockState());
            return Optional.empty();
        }

        return Optional.of(Util.getBlock(new ResourceLocation(replacementId)).defaultBlockState());
    }
}