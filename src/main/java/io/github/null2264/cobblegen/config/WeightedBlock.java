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
        if (dimensions != null) {
            buf.writeInt(dimensions.size());
            for (String dimId : dimensions) {
                buf.writeUtf(dimId);
            }
        } else buf.writeInt(0);
        if (excludedDimensions != null) {
            buf.writeInt(excludedDimensions.size());
            for (String dimId : excludedDimensions) {
                buf.writeUtf(dimId);
            }
        } else buf.writeInt(0);
        buf.writeInt(maxY == null ? Level.MAX_ENTITY_SPAWN_Y : maxY);
        buf.writeInt(minY == null ? Level.MIN_ENTITY_SPAWN_Y : minY);
    }

    public static WeightedBlock fromPacket(FriendlyByteBuf buf) {
        val id = buf.readUtf();
        val weight = buf.readDouble();
        val _dimSize = buf.readInt();
        List<String> dimensions = null;
        if (_dimSize > 0) {
            dimensions = new ArrayList<>(_dimSize);
            for (int i = 0; i < _dimSize; i++) {
                dimensions.add(buf.readUtf());
            }
        }
        val _exDimSize = buf.readInt();
        List<String> excludedDimensions = null;
        if (_exDimSize > 0) {
            excludedDimensions = new ArrayList<>(_exDimSize);
            for (int i = 0; i < _dimSize; i++) {
                excludedDimensions.add(buf.readUtf());
            }
        }
        Integer maxY = buf.readInt();
        if (maxY == Level.MAX_ENTITY_SPAWN_Y) maxY = null;
        Integer minY = buf.readInt();
        if (minY == Level.MIN_ENTITY_SPAWN_Y) minY = null;
        return new WeightedBlock(id, weight, dimensions, excludedDimensions, maxY, minY);
    }
}