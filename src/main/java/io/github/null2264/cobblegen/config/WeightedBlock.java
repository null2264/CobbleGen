package io.github.null2264.cobblegen.config;

import io.github.null2264.cobblegen.data.model.PacketSerializable;
import io.github.null2264.cobblegen.util.Util;
import lombok.val;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class WeightedBlock implements PacketSerializable<WeightedBlock>
{
    public String id;
    public Double weight;
    @Nullable
    public List<String> dimensions;
    @Nullable
    public List<String> excludedDimensions;
    @Nullable
    public Integer maxY;
    @Nullable
    public Integer minY;

    public WeightedBlock(String id, Double weight) {
        this(id, weight, null, null);
    }

    public WeightedBlock(String id, Double weight, List<String> dimIds) {
        this(id, weight, dimIds, null);
    }

    public WeightedBlock(String id, Double weight, List<String> dimIds, List<String> excludedDimensions) {
        this(id, weight, dimIds, excludedDimensions, null, null);
    }

    public WeightedBlock(
            String id,
            Double weight,
            @Nullable List<String> dimIds,
            @Nullable List<String> excludedDimensions,
            @Nullable Integer maxY,
            @Nullable Integer minY
    ) {
        this.id = id;
        this.weight = weight;
        this.dimensions = dimIds;
        this.excludedDimensions = excludedDimensions;
        this.maxY = maxY;
        this.minY = minY;
    }

    public static WeightedBlock fromBlock(Block block, Double weight) {
        return fromBlock(block, weight, null, null, null, null);
    }

    public Optional<List<String>> getDimensions() {
        return Util.optional(dimensions);
    }

    public static WeightedBlock fromBlock(
            Block block,
            Double weight,
            List<String> dimIds,
            List<String> excludedDimensions,
            Integer maxY,
            Integer minY
    ) {
        val id = Util.getBlockId(block).toString();
        return new WeightedBlock(id, weight, dimIds, excludedDimensions, maxY, minY);
    }

    public Block getBlock() {
        return Util.getBlock(ResourceLocation.tryParse(id));
    }

    public Optional<List<String>> getExcludedDimensions() {
        return Util.optional(excludedDimensions);
    }

    public Optional<Integer> getMaxY() {
        return Util.optional(maxY);
    }

    public Optional<Integer> getMinY() {
        return Util.optional(minY);
    }

    @Override
    public void toPacket(FriendlyByteBuf buf) {
        buf.writeUtf(id);
        buf.writeDouble(weight);

        buf.writeOptional(Util.optional(dimensions), (o, value) -> o.writeCollection(value, FriendlyByteBuf::writeUtf));
        buf.writeOptional(Util.optional(excludedDimensions), (o, value) -> o.writeCollection(value, FriendlyByteBuf::writeUtf));

        buf.writeOptional(Util.optional(maxY), FriendlyByteBuf::writeInt);
        buf.writeOptional(Util.optional(minY), FriendlyByteBuf::writeInt);
    }

    public static WeightedBlock fromPacket(FriendlyByteBuf buf) {
        val id = buf.readUtf();
        val weight = buf.readDouble();

        Optional<List<String>> dimensions = buf.readOptional((o) -> o.readList(FriendlyByteBuf::readUtf));
        Optional<List<String>> excludedDimensions = buf.readOptional((o) -> o.readList(FriendlyByteBuf::readUtf));

        Optional<Integer> maxY = buf.readOptional(FriendlyByteBuf::readInt);
        Optional<Integer> minY = buf.readOptional(FriendlyByteBuf::readInt);

        return new WeightedBlock(
                id,
                weight,
                dimensions.orElse(null),
                excludedDimensions.orElse(null),
                maxY.orElse(null),
                minY.orElse(null)
        );
    }
}