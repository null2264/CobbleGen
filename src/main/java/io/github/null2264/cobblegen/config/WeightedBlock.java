package io.github.null2264.cobblegen.config;

import io.github.null2264.cobblegen.data.model.PacketSerializable;
import io.github.null2264.cobblegen.util.Util;
import lombok.val;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class WeightedBlock implements PacketSerializable<WeightedBlock>
{
    public String id;
    public Double weight;
    public List<String> dimensions;
    public List<String> excludedDimensions;
    public Integer maxY;
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
            List<String> dimIds,
            List<String> excludedDimensions,
            Integer maxY,
            Integer minY
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

    @Override
    public void toPacket(FriendlyByteBuf buf) {
        buf.writeUtf(id);
        buf.writeDouble(weight);

        buf.writeCollection(Util.notNullOr(dimensions, List.of()), FriendlyByteBuf::writeUtf);
        buf.writeCollection(Util.notNullOr(excludedDimensions, List.of()), FriendlyByteBuf::writeUtf);

        buf.writeOptional(Util.optional(maxY), FriendlyByteBuf::writeInt);
        buf.writeOptional(Util.optional(minY), FriendlyByteBuf::writeInt);
    }

    public static WeightedBlock fromPacket(FriendlyByteBuf buf) {
        val id = buf.readUtf();
        val weight = buf.readDouble();

        List<String> dimensions = buf.readList(FriendlyByteBuf::readUtf);
        List<String> excludedDimensions = buf.readList(FriendlyByteBuf::readUtf);

        Optional<Integer> maxY = buf.readOptional(FriendlyByteBuf::readInt);
        Optional<Integer> minY = buf.readOptional(FriendlyByteBuf::readInt);

        return new WeightedBlock(id, weight, dimensions, excludedDimensions, maxY.orElse(null), minY.orElse(null));
    }
}