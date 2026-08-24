package io.github.null2264.cobblegen.data.model;

import io.github.null2264.cobblegen.data.CGIdentifier;
import io.github.null2264.cobblegen.data.config.ConfigMetaData;
import io.github.null2264.cobblegen.data.config.GeneratorMap;
import io.github.null2264.cobblegen.data.config.ResultList;
import io.github.null2264.cobblegen.data.config.WeightedBlock;
import io.github.null2264.cobblegen.util.CGLog;
import io.github.null2264.cobblegen.util.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

import static io.github.null2264.cobblegen.mc.Constants.DIRECTIONS;

@ApiStatus.Internal
public interface BuiltInGenerator extends Generator {

    // https://stackoverflow.com/a/6737362
    @NotNull
    #if MC<=11605
    @ApiStatus.Internal
    default Optional<String>
    #else
    private Optional<String>
    #endif
    randomizeBlockId(ResultList blockIds, String dim, Integer yLevel, @Nullable String biome) {
        ResultList filteredBlockIds = new ResultList();
        AtomicReference<Double> totalWeight = new AtomicReference<>(0.0);

        for (WeightedBlock block : blockIds) {
            if (block.dimensions != null && !block.dimensions.contains(dim)) continue;

            if (block.excludedDimensions != null && block.excludedDimensions.contains(dim)) continue;

            if (biome != null && ConfigMetaData.INSTANCE.enableExperimentalFeatures) {
                if (block.biomes != null && !block.biomes.contains(biome)) continue;

                if (block.excludedBiomes != null && block.excludedBiomes.contains(biome)) continue;
            }

            if (block.maxY != null && block.maxY <= yLevel) continue;

            if (block.minY != null && block.minY >= yLevel) continue;

            if (block.id.startsWith("#")) {
                List<CGIdentifier> taggedBlocks = Util.getTaggedBlockIds(CGIdentifier.of(block.id.substring(1)));
                for (CGIdentifier taggedBlock : taggedBlocks) {
                    WeightedBlock actualBlock = null;
                    try {
                        actualBlock = new WeightedBlock.Builder().setId(taggedBlock.toMC().toString()).setWeight(block.weight).build();
                    } catch (Exception ignored) {
                    }
                    if (actualBlock == null) continue;
                    filteredBlockIds.add(block);
                    totalWeight.updateAndGet(v -> v + block.weight);
                }
            } else {
                filteredBlockIds.add(block);
                totalWeight.updateAndGet(v -> v + block.weight);
            }
        }

        if (filteredBlockIds.isEmpty()) return Optional.empty();

        if (filteredBlockIds.size() == 1) return Util.optional(filteredBlockIds.get(0).id);

        int idx = 0;
        for (double r = Math.random() * totalWeight.get(); idx < filteredBlockIds.size() - 1; ++idx) {
            r -= filteredBlockIds.get(idx).weight;
            if (r <= 0.0) break;
        }

        return Util.optional(filteredBlockIds.get(idx).id);
    }

    default Optional<BlockState> getBlockCandidate(LevelAccessor level, BlockPos pos, GeneratorMap candidates, Block defaultBlock, Boolean isLenient) {
        Optional<ResultList> resultCandidates = Optional.empty();
        if (ConfigMetaData.INSTANCE.enableExperimentalFeatures) {
            for (Direction direction : DIRECTIONS) {
                Block key = level.getBlockState(pos.relative(direction)).getBlock();
                CGIdentifier id = Util.getBlockId(key);
                if (!candidates.containsKey(id)) continue;

                ResultList candidateList = new ResultList();
                candidates.get(id).forEach((block) -> {
                    if (
                        block.getLenientModifier().isPresent() &&
                        !block.getLenientModifier().get() &&
                        direction != Direction.DOWN
                    ) return;
                    candidateList.add(block);
                });
                if (candidateList.isEmpty()) continue;

                resultCandidates = Util.optional(candidateList);
                break;
            }
        } else {
            Block key = level.getBlockState(pos.below()).getBlock();
            CGIdentifier id = Util.getBlockId(key);
            resultCandidates = Util.optional(candidates.get(id));
        }

        String dim = Util.getDimension(level);
        CGLog.debug(dim);
        String biome = Util.getBiome(level, pos);
        CGLog.debug(biome);

        Optional<String> replacementId = randomizeBlockId(
            resultCandidates.orElseGet(() -> candidates.getOrDefault(CGIdentifier.wildcard(), new ResultList())),
            dim,
            pos.getY(),
            biome
        );

        if (!replacementId.isPresent()) {
            if (defaultBlock != null)
                return Util.optional(defaultBlock.defaultBlockState());
            return Optional.empty();
        }

        CGIdentifier id = CGIdentifier.of(replacementId.get());
        Block block;
        try {
            block = Util.getBlock(id);
        } catch (Exception e) {
            block = null;
        }
        return Util.optional(block.defaultBlockState());
    }
}
