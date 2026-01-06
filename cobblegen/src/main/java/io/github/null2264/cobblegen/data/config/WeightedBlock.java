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

import static io.github.null2264.cobblegen.data.config.Config.JANKSON;

public class WeightedBlock implements PacketSerializable<WeightedBlock>, JanksonSerializable {

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
    @Nullable
    public Boolean lenientModifier;

    /**
     * @deprecated Use {@link WeightedBlock.Builder} instead.
     */
    @Deprecated
    public WeightedBlock(String id, Double weight) {
        this(id, weight, null, null);
    }

    /**
     * @deprecated Use {@link WeightedBlock.Builder} instead.
     */
    @Deprecated
    public WeightedBlock(String id, Double weight, List<String> dimIds) {
        this(id, weight, dimIds, null);
    }

    /**
     * @deprecated Use {@link WeightedBlock.Builder} instead.
     */
    @Deprecated
    public WeightedBlock(String id, Double weight, List<String> dimIds, List<String> excludedDimensions) {
        this(id, weight, dimIds, excludedDimensions, null, null, null, null, null, null);
    }

    /**
     * @deprecated Use {@link WeightedBlock.Builder} instead.
     */
    @Deprecated
    public WeightedBlock(
            String id,
            Double weight,
            @Nullable List<String> dimIds,
            @Nullable List<String> excludedDimensions,
            @Nullable Integer maxY,
            @Nullable Integer minY,
            @Nullable List<String> neighbours,
            @Nullable List<String> biomes,
            @Nullable List<String> excludedBiomes,
            @Nullable Boolean lenientModifier
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
        this.lenientModifier = lenientModifier;
    }

    /**
     * @deprecated Use {@link WeightedBlock.Builder#of(Block)} instead.
     */
    @Deprecated
    public static WeightedBlock fromBlock(Block block, Double weight) {
        return fromBlock(block, weight, null, null, null, null);
    }

    /**
     * @deprecated Use {@link WeightedBlock.Builder#of(Block)} instead.
     */
    @Deprecated
    public static WeightedBlock fromBlock(
            Block block,
            Double weight,
            List<String> dimIds,
            List<String> excludedDimensions,
            Integer maxY,
            Integer minY
    ) {
        final String id = Util.getBlockId(block).toString();
        return new WeightedBlock(id, weight, dimIds, excludedDimensions, maxY, minY, null, null, null, null);
    }

    public Block getBlock() {
        return Util.getBlock(ResourceLocation.tryParse(id));
    }

    public Optional<List<String>> getDimensions() {
        return Util.optional(dimensions);
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

    public Optional<List<String>> getBiomes() {
        return Util.optional(biomes);
    }

    public Optional<List<String>> getExcludedBiomes() {
        return Util.optional(excludedBiomes);
    }

    public Optional<List<String>> getNeighbours() {
        return Util.optional(neighbours);
    }

    public Optional<Boolean> getLenientModifier() {
        return Util.optional(lenientModifier);
    }

    @Override
    public void toPacket(FriendlyByteBuf buf) {
        buf.writeUtf(id);
        buf.writeDouble(weight);

        buf.writeOptional(getDimensions(), (o, value) -> o.writeCollection(value, FriendlyByteBuf::writeUtf));
        buf.writeOptional(getExcludedDimensions(), (o, value) -> o.writeCollection(value, FriendlyByteBuf::writeUtf));

        buf.writeOptional(getMaxY(), FriendlyByteBuf::writeInt);
        buf.writeOptional(getMinY(), FriendlyByteBuf::writeInt);

        buf.writeOptional(getBiomes(), (o, value) -> o.writeCollection(value, FriendlyByteBuf::writeUtf));
        buf.writeOptional(getExcludedBiomes(), (o, value) -> o.writeCollection(value, FriendlyByteBuf::writeUtf));

        //buf.writeOptional(getLenientModifier(), FriendlyByteBuf::writeBoolean);
    }

    public static WeightedBlock fromPacket(FriendlyByteBuf buf) {
        WeightedBlock.Builder builder = new WeightedBlock.Builder();

        builder.setId(buf.readUtf());
        builder.setWeight(buf.readDouble());

        buf.readOptional((o) -> o.readList(FriendlyByteBuf::readUtf)).ifPresent(builder::setDimensions);
        buf.readOptional((o) -> o.readList(FriendlyByteBuf::readUtf)).ifPresent(builder::setExcludedDimensions);

        buf.readOptional(FriendlyByteBuf::readInt).ifPresent(builder::setMaxY);
        buf.readOptional(FriendlyByteBuf::readInt).ifPresent(builder::setMinY);

        buf.readOptional((o) -> o.readList(FriendlyByteBuf::readUtf)).ifPresent(builder::setBiomes);
        buf.readOptional((o) -> o.readList(FriendlyByteBuf::readUtf)).ifPresent(builder::setExcludedBiomes);

        //buf.readOptional(FriendlyByteBuf::readBoolean).ifPresent(builder::setLenientModifier);

        return builder.build();
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
        json.put("lenientModifier", JANKSON.toJson(lenientModifier));
        return json;
    }

    @SuppressWarnings("PatternVariableCanBeUsed")
    @Deserializer
    public static WeightedBlock fromJson(JsonObject json) {
        Builder builder = new WeightedBlock.Builder();

        JsonElement _id = json.get("id");
        if (!(_id instanceof JsonPrimitive)) return null;
        builder.setId(((JsonPrimitive) _id).asString());
        builder.setWeight(json.getDouble("weight", 0.0));

        JsonElement _dimensions = json.get("dimensions");
        if (_dimensions instanceof JsonArray) {
            List<String> dimensions = new ArrayList<>();
            ((JsonArray) _dimensions).forEach(value -> dimensions.add(((JsonPrimitive) value).asString()));
            builder.setDimensions(dimensions);
        }

        JsonElement _excludedDimensions = json.get("excludedDimensions");
        if (_excludedDimensions instanceof JsonArray) {
            List<String> excludedDimensions = new ArrayList<>();
            ((JsonArray) _excludedDimensions).forEach(value -> excludedDimensions.add(((JsonPrimitive) value).asString()));
            builder.setExcludedDimensions(excludedDimensions);
        }

        JsonElement _maxY = json.get("maxY");
        if (_maxY instanceof JsonPrimitive) {
            builder.setMaxY(((JsonPrimitive) _maxY).asInt(0));
        }

        JsonElement _minY = json.get("minY");
        if (_minY instanceof JsonPrimitive) {
            builder.setMinY(((JsonPrimitive) _minY).asInt(0));
        }

        JsonElement _biomes = json.get("biomes");
        if (_biomes instanceof JsonArray) {
            List<String> biomes = new ArrayList<>();
            ((JsonArray) _biomes).forEach(value -> biomes.add(((JsonPrimitive) value).asString()));
            builder.setBiomes(biomes);
        }

        JsonElement _excludedBiomes = json.get("excludedBiomes");
        if (_excludedBiomes instanceof JsonArray) {
            List<String> excludedBiomes = new ArrayList<>();
            ((JsonArray) _excludedBiomes).forEach(value -> excludedBiomes.add(((JsonPrimitive) value).asString()));
            builder.setExcludedBiomes(excludedBiomes);
        }

        JsonElement _lenientModifier = json.get("lenientModifier");
        if (_lenientModifier instanceof JsonPrimitive) {
            builder.setLenientModifier(((JsonPrimitive) _lenientModifier).asBoolean(false));
        }

        return builder.build();
    }

    public static class Builder {
        @Nullable
        public String id;
        @Nullable
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
        @Nullable
        public Boolean lenientModifier;

        public static Builder of(Block block) {
            return new Builder().setId(Util.getBlockId(block).toString());
        }

        public WeightedBlock build() {
            if (id == null && weight == null) {
                throw new IllegalStateException("Block ID and generation weight can't be unset!");
            }

            return new WeightedBlock(
                id,
                weight,
                dimensions,
                excludedDimensions,
                maxY,
                minY,
                neighbours,
                biomes,
                excludedBiomes,
                lenientModifier
            );
        }

        public Builder setId(String value) {
            this.id = value;
            return this;
        }

        public Builder setWeight(Double value) {
            this.weight = value;
            return this;
        }

        public Builder setDimensions(List<String> value) {
            this.dimensions = value;
            return this;
        }

        public Builder setExcludedDimensions(List<String> value) {
            this.excludedDimensions = value;
            return this;
        }

        public Builder setMaxY(Integer value) {
            this.maxY = value;
            return this;
        }

        public Builder setMinY(Integer value) {
            this.minY = value;
            return this;
        }

        public Builder setBiomes(List<String> value) {
            this.biomes = value;
            return this;
        }

        public Builder setExcludedBiomes(List<String> value) {
            this.excludedBiomes = value;
            return this;
        }

        public Builder setLenientModifier(Boolean value) {
            this.lenientModifier = value;
            return this;
        }
    }
}
