package io.github.null2264.cobblegen.data.config;

import blue.endless.jankson.JsonArray;
import blue.endless.jankson.JsonElement;
import blue.endless.jankson.JsonObject;
import blue.endless.jankson.JsonPrimitive;
import blue.endless.jankson.annotation.Deserializer;
import blue.endless.jankson.annotation.Serializer;
import io.github.null2264.cobblegen.data.JanksonSerializable;
import io.github.null2264.cobblegen.data.model.PacketSerializable;
import io.github.null2264.cobblegen.util.Util;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static io.github.null2264.cobblegen.util.Constants.JANKSON;

public class WeightedBlock implements PacketSerializable<WeightedBlock>, JanksonSerializable
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
    @Nullable
    public List<String> biomes;
    @Nullable
    public List<String> excludedBiomes;

    public WeightedBlock(String id, Double weight) {
        this(id, weight, null, null);
    }

    public WeightedBlock(String id, Double weight, List<String> dimIds) {
        this(id, weight, dimIds, null);
    }

    public WeightedBlock(String id, Double weight, List<String> dimIds, List<String> excludedDimensions) {
        this(id, weight, dimIds, excludedDimensions, null, null, null, null, null);
    }

    public WeightedBlock(
            String id,
            Double weight,
            @Nullable List<String> dimIds,
            @Nullable List<String> excludedDimensions,
            @Nullable Integer maxY,
            @Nullable Integer minY,
            @Nullable List<String> neighbours,
            @Nullable List<String> biomes,
            @Nullable List<String> excludedBiomes
    ) {
        this.id = id;
        this.weight = weight;
        this.dimensions = dimIds;
        this.excludedDimensions = excludedDimensions;
        this.maxY = maxY;
        this.minY = minY;
        this.neighbours = neighbours;
        this.biomes = biomes;
        this.excludedBiomes = excludedBiomes;
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
        return new WeightedBlock(id, weight, dimIds, excludedDimensions, maxY, minY, null, null, null);
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

        buf.writeOptional(Util.optional(biomes), (o, value) -> o.writeCollection(value, FriendlyByteBuf::writeUtf));
        buf.writeOptional(Util.optional(excludedBiomes), (o, value) -> o.writeCollection(value, FriendlyByteBuf::writeUtf));
    }

    public static WeightedBlock fromPacket(FriendlyByteBuf buf) {
        final String id = buf.readUtf();
        final Double weight = buf.readDouble();

        Optional<List<String>> dimensions = buf.readOptional((o) -> o.readList(FriendlyByteBuf::readUtf));
        Optional<List<String>> excludedDimensions = buf.readOptional((o) -> o.readList(FriendlyByteBuf::readUtf));

        Optional<Integer> maxY = buf.readOptional(FriendlyByteBuf::readInt);
        Optional<Integer> minY = buf.readOptional(FriendlyByteBuf::readInt);

        Optional<List<String>> biomes = buf.readOptional((o) -> o.readList(FriendlyByteBuf::readUtf));
        Optional<List<String>> excludedBiomes = buf.readOptional((o) -> o.readList(FriendlyByteBuf::readUtf));

        return new WeightedBlock(
                id,
                weight,
                dimensions.orElse(null),
                excludedDimensions.orElse(null),
                maxY.orElse(null),
                minY.orElse(null),
                null,
                biomes.orElse(null),
                excludedBiomes.orElse(null)
        );
    }

    @Override
    @Serializer
    public JsonObject toJson() {
        JsonObject json = new JsonObject();
        json.put("id", JsonPrimitive.of(id));
        json.put("weight", JsonPrimitive.of(weight));
        json.put("dimensions", JANKSON.toJson(dimensions));
        json.put("excludedDimensions", JANKSON.toJson(excludedDimensions));
        json.put("maxY", JANKSON.toJson(maxY));
        json.put("minY", JANKSON.toJson(minY));
        json.put("biomes", JANKSON.toJson(biomes));
        json.put("excludedBiomes", JANKSON.toJson(excludedBiomes));
        return json;
    }

    @SuppressWarnings("PatternVariableCanBeUsed")
    @Deserializer
    public static WeightedBlock fromJson(JsonObject json) {
        JsonElement _id = json.get("id");
        if (!(_id instanceof JsonPrimitive)) return null;
        String id = ((JsonPrimitive) _id).asString();

        Double weight = json.getDouble("weight", 0.0);

        @Nullable
        List<String> dimensions;
        JsonElement _dimensions = json.get("dimensions");
        if (_dimensions instanceof JsonArray) {
            dimensions = new ArrayList<>();
            ((JsonArray) _dimensions).forEach(value -> dimensions.add(((JsonPrimitive) value).asString()));
        } else {
            dimensions = null;
        }

        @Nullable
        List<String> excludedDimensions;
        JsonElement _excludedDimensions = json.get("excludedDimensions");
        if (_excludedDimensions instanceof JsonArray) {
            excludedDimensions = new ArrayList<>();
            ((JsonArray) _excludedDimensions).forEach(value -> excludedDimensions.add(((JsonPrimitive) value).asString()));
        } else {
            excludedDimensions = null;
        }

        @Nullable
        Integer maxY = null;
        JsonElement _maxY = json.get("maxY");
        if (_maxY instanceof JsonPrimitive) {
            maxY = ((JsonPrimitive) _maxY).asInt(0);
        }

        @Nullable
        Integer minY = null;
        JsonElement _minY = json.get("minY");
        if (_minY instanceof JsonPrimitive) {
            minY = ((JsonPrimitive) _minY).asInt(0);
        }

        @Nullable
        List<String> biomes;
        JsonElement _biomes = json.get("biomes");
        if (_biomes instanceof JsonArray) {
            biomes = new ArrayList<>();
            ((JsonArray) _biomes).forEach(value -> biomes.add(((JsonPrimitive) value).asString()));
        } else {
            biomes = null;
        }

        @Nullable
        List<String> excludedBiomes;
        JsonElement _excludedBiomes = json.get("excludedBiomes");
        if (_excludedBiomes instanceof JsonArray) {
            excludedBiomes = new ArrayList<>();
            ((JsonArray) _excludedBiomes).forEach(value -> excludedBiomes.add(((JsonPrimitive) value).asString()));
        } else {
            excludedBiomes = null;
        }

        return new WeightedBlock(id, weight, dimensions, excludedDimensions, maxY, minY, null, biomes, excludedBiomes);
    }
}
