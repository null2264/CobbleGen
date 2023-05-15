package io.github.null2264.cobblegen.config;

import io.github.null2264.cobblegen.data.model.PacketSerializable;
import lombok.val;
import net.minecraft.block.Block;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

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
        if (dimensions != null) {
            buf.writeInt(dimensions.size());
            for (String dimId : dimensions) {
                buf.writeString(dimId);
            }
        } else buf.writeInt(0);
        if (excludedDimensions != null) {
            buf.writeInt(excludedDimensions.size());
            for (String dimId : excludedDimensions) {
                buf.writeString(dimId);
            }
        } else buf.writeInt(0);
        buf.writeInt(maxY == null ? World.MAX_Y : maxY);
        buf.writeInt(minY == null ? World.MIN_Y : minY);
    }

    public static WeightedBlock fromPacket(PacketByteBuf buf) {
        val id = buf.readString();
        val weight = buf.readDouble();
        val _dimSize = buf.readInt();
        List<String> dimensions = null;
        if (_dimSize > 0) {
            dimensions = new ArrayList<>(_dimSize);
            for (int i = 0; i < _dimSize; i++) {
                dimensions.add(buf.readString());
            }
        }
        val _exDimSize = buf.readInt();
        List<String> excludedDimensions = null;
        if (_exDimSize > 0) {
            excludedDimensions = new ArrayList<>(_exDimSize);
            for (int i = 0; i < _dimSize; i++) {
                excludedDimensions.add(buf.readString());
            }
        }
        Integer maxY = buf.readInt();
        if (maxY == World.MAX_Y) maxY = null;
        Integer minY = buf.readInt();
        if (minY == World.MIN_Y) minY = null;
        return new WeightedBlock(id, weight, dimensions, excludedDimensions, maxY, minY);
    }
}