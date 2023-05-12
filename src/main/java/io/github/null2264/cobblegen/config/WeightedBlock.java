package io.github.null2264.cobblegen.config;

import io.github.null2264.cobblegen.data.model.PacketSerializable;
import lombok.val;
import net.minecraft.block.Block;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

import java.sql.Array;
import java.util.ArrayList;
import java.util.List;

import static io.github.null2264.cobblegen.CobbleGen.getCompat;

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
        val id = getCompat().getBlockId(block).toString();
        return new WeightedBlock(id, weight, dimIds, excludedDimensions, maxY, minY);
    }

    public Block getBlock() {
        return getCompat().getBlock(Identifier.tryParse(id));
    }

    @Override
    public void toPacket(PacketByteBuf buf) {
        buf.writeString(id);
        buf.writeDouble(weight);
        buf.writeInt(dimensions.size());
        for (String dimId : dimensions) {
            buf.writeString(dimId);
        }
        buf.writeInt(excludedDimensions.size());
        for (String dimId : excludedDimensions) {
            buf.writeString(dimId);
        }
        buf.writeInt(maxY);
        buf.writeInt(minY);
    }

    public static WeightedBlock fromPacket(PacketByteBuf buf) {
        val id = buf.readString();
        val weight = buf.readDouble();
        val _dimSize = buf.readInt();
        val dimensions = new ArrayList<String>(_dimSize);
        for (int i = 0; i < _dimSize; i++) {
            dimensions.add(buf.readString());
        }
        val _exDimSize = buf.readInt();
        val excludedDimensions = new ArrayList<String>(_exDimSize);
        for (int i = 0; i < _dimSize; i++) {
            excludedDimensions.add(buf.readString());
        }
        val maxY = buf.readInt();
        val minY = buf.readInt();
        return new WeightedBlock(id, weight, dimensions, excludedDimensions, maxY, minY);
    }
}