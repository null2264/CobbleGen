package io.github.null2264.cobblegen.data.model;

import io.github.null2264.cobblegen.data.CGIdentifier;
import io.github.null2264.cobblegen.data.config.ConfigMetaData;
import io.github.null2264.cobblegen.data.config.GeneratorMap;
import io.github.null2264.cobblegen.data.config.ResultList;
import io.github.null2264.cobblegen.data.config.WeightedBlock;
import io.github.null2264.cobblegen.util.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

import static io.github.null2264.cobblegen.mc.Constants.FLOW_DIRECTIONS;
import static io.github.null2264.cobblegen.util.Util.identifierOf;

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
                try {
                    List<ResourceLocation> taggedBlocks = Util.getTaggedBlockIds(ResourceLocation.tryParse(block.id.substring(1)));
                    for (ResourceLocation taggedBlock : taggedBlocks) {
                        filteredBlockIds.add(new WeightedBlock.Builder().setId(taggedBlock.toString()).setWeight(block.weight).build());
                        totalWeight.updateAndGet(v -> v + block.weight);
                    }
                } catch (Exception ignored) {
                }
            } else {
                filteredBlockIds.add(block);
                totalWeight.updateAndGet(v -> v + block.weight);
            }
        }

        if (filteredBlockIds.isEmpty()) return Optional.empty();

        if (filteredBlockIds.size() == 1) return Optional.of(filteredBlockIds.get(0).id);

        int idx = 0;
        for (double r = Math.random() * totalWeight.get(); idx < filteredBlockIds.size() - 1; ++idx) {
            r -= filteredBlockIds.get(idx).weight;
            if (r <= 0.0) break;
        }

        return Optional.of(filteredBlockIds.get(idx).id);
    }

    default Optional<BlockState> getBlockCandidate(LevelAccessor level, BlockPos pos, GeneratorMap candidates, Block defaultBlock, Boolean isLenient) {
        Optional<ResultList> resultCandidates = Optional.empty();
        if (isLenient && ConfigMetaData.INSTANCE.enableExperimentalFeatures) {
            for (Direction direction : FLOW_DIRECTIONS) {
                Block key = level.getBlockState(pos.relative(direction)).getBlock();
                CGIdentifier id = CGIdentifier.fromMC(Util.getBlockId(key));
                if (!candidates.containsKey(id)) continue;
                resultCandidates = Optional.of(candidates.get(id));
            }
        } else {
            Block key = level.getBlockState(pos.below()).getBlock();
            CGIdentifier id = CGIdentifier.fromMC(Util.getBlockId(key));
            resultCandidates = Optional.of(candidates.get(id));
        }

        Optional<String> replacementId = randomizeBlockId(
            resultCandidates.orElseGet(() -> candidates.getOrDefault(CGIdentifier.wildcard(), new ResultList())),
            Util.getDimension(level),
            pos.getY(),
            Util.getBiome(level, pos)
        );

        if (replacementId.isEmpty()) {
            if (defaultBlock != null)
                return Optional.of(defaultBlock.defaultBlockState());
            return Optional.empty();
        }

        ResourceLocation id;
        try {
            id = ResourceLocation.tryParse(replacementId.get());
        } catch (Exception e) {
            id = identifierOf(replacementId.get());
        }
        return Optional.of(Util.getBlock(id).defaultBlockState());
    }
}
