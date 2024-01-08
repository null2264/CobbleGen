package io.github.null2264.cobblegen.config;

import io.github.null2264.cobblegen.compat.ByteBufCompat;
import io.github.null2264.cobblegen.data.model.PacketSerializable;
import io.github.null2264.cobblegen.util.Util;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.Nullable;

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
    @Nullable
    public List<String> neighbours;

    public WeightedBlock(String id, Double weight) {
        this(id, weight, null, null);
    }

    public WeightedBlock(String id, Double weight, List<String> dimIds) {
        this(id, weight, dimIds, null);
    }

    public WeightedBlock(String id, Double weight, List<String> dimIds, List<String> excludedDimensions) {
        this(id, weight, dimIds, excludedDimensions, null, null, null);
    }

    public WeightedBlock(
            String id,
            Double weight,
            @Nullable List<String> dimIds,
            @Nullable List<String> excludedDimensions,
            @Nullable Integer maxY,
            @Nullable Integer minY,
            @Nullable List<String> neighbours
    ) {
        this.id = id;
        this.weight = weight;
        this.dimensions = dimIds;
        this.excludedDimensions = excludedDimensions;
        this.maxY = maxY;
        this.minY = minY;
        this.neighbours = neighbours;
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
        final String id = Util.getBlockId(block).toString();
        return new WeightedBlock(id, weight, dimIds, excludedDimensions, maxY, minY, null);
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

    @SuppressWarnings("RedundantCast")
    @Override
    public void toPacket(ByteBufCompat buf) {
        buf.writeUtf(id);
        buf.writeDouble(weight);

        buf.writeOptional(Util.optional(dimensions), (o, value) -> ((ByteBufCompat) o).writeCollection(value, FriendlyByteBuf::writeUtf));
        buf.writeOptional(Util.optional(excludedDimensions), (o, value) -> ((ByteBufCompat) o).writeCollection(value, FriendlyByteBuf::writeUtf));

        buf.writeOptional(Util.optional(maxY), FriendlyByteBuf::writeInt);
        buf.writeOptional(Util.optional(minY), FriendlyByteBuf::writeInt);
    }

    @SuppressWarnings("RedundantCast")
    public static WeightedBlock fromPacket(FriendlyByteBuf buf) {
        final String id = buf.readUtf();
        final Double weight = buf.readDouble();

        Optional<List<String>> dimensions = ((ByteBufCompat) buf).readOptional((o) -> ((ByteBufCompat) o).readList(FriendlyByteBuf::readUtf));
        Optional<List<String>> excludedDimensions = ((ByteBufCompat) buf).readOptional((o) -> ((ByteBufCompat) o).readList(FriendlyByteBuf::readUtf));

        Optional<Integer> maxY = ((ByteBufCompat) buf).readOptional(FriendlyByteBuf::readInt);
        Optional<Integer> minY = ((ByteBufCompat) buf).readOptional(FriendlyByteBuf::readInt);

        return new WeightedBlock(
                id,
                weight,
                dimensions.orElse(null),
                excludedDimensions.orElse(null),
                maxY.orElse(null),
                minY.orElse(null),
                null
        );
    }
}