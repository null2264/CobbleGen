package io.github.null2264.cobblegen.data.model;

import io.github.null2264.cobblegen.CobbleGen;
import io.github.null2264.cobblegen.data.CGIdentifier;
import io.github.null2264.cobblegen.data.config.GeneratorMap;
import io.github.null2264.cobblegen.data.config.ResultList;
import io.github.null2264.cobblegen.data.config.WeightedBlock;
import io.github.null2264.cobblegen.util.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

import static io.github.null2264.cobblegen.util.Util.identifierOf;

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
    randomizeBlockId(Block key, String dim, Integer yLevel, GeneratorMap candidates, @Nullable String biome) {
        ResultList blockIds = candidates.getOrDefault(
                CGIdentifier.fromMC(Util.getBlockId(key)),
                candidates.getOrDefault(CGIdentifier.wildcard(), new ResultList())
        );

        ResultList filteredBlockIds = new ResultList();
        AtomicReference<Double> totalWeight = new AtomicReference<>(0.0);

        for (WeightedBlock block : blockIds) {
            if (block.dimensions != null && !block.dimensions.contains(dim)) continue;

            if (block.excludedDimensions != null && block.excludedDimensions.contains(dim)) continue;

            if (biome != null && CobbleGen.META_CONFIG.enableExperimentalFeatures) {
                if (block.biomes != null && !block.biomes.contains(biome)) continue;

                if (block.excludedBiomes != null && block.excludedBiomes.contains(biome)) continue;
            }

            if (block.maxY != null && block.maxY <= yLevel) continue;

            if (block.minY != null && block.minY >= yLevel) continue;

            if (block.id.startsWith("#")) {
                try {
                    List<ResourceLocation> taggedBlocks = Util.getTaggedBlockIds(ResourceLocation.tryParse(block.id.substring(1)));
                    for (ResourceLocation taggedBlock : taggedBlocks) {
                        filteredBlockIds.add(new WeightedBlock(taggedBlock.toString(), block.weight));
                        totalWeight.updateAndGet(v -> v + block.weight);
                    }
                } catch (Exception ignored) {
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

    default Optional<BlockState> getBlockCandidate(LevelAccessor level, BlockPos pos, GeneratorMap candidates) {
        return getBlockCandidate(level, pos, candidates, null);
    }

    default Optional<BlockState> getBlockCandidate(LevelAccessor level, BlockPos pos, GeneratorMap candidates, Block defaultBlock) {
        String replacementId = randomizeBlockId(
                level.getBlockState(pos.below()).getBlock(),
                Util.getDimension(level),
                pos.getY(),
                candidates,
                Util.getBiome(level, pos)
        );

        if (replacementId == null) {
            if (defaultBlock != null)
                return Optional.of(defaultBlock.defaultBlockState());
            return Optional.empty();
        }

        ResourceLocation id;
        try {
            id = ResourceLocation.tryParse(replacementId);
        } catch (Exception e) {
            id = identifierOf(replacementId);
        }
        return Optional.of(Util.getBlock(id).defaultBlockState());
    }
}
