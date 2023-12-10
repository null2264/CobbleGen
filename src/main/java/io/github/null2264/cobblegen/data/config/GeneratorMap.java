package io.github.null2264.cobblegen.data.config;

import blue.endless.jankson.JsonArray;
import blue.endless.jankson.JsonElement;
import blue.endless.jankson.JsonObject;
import blue.endless.jankson.annotation.Deserializer;
import blue.endless.jankson.annotation.Serializer;
import com.google.common.primitives.Ints;
import io.github.null2264.cobblegen.data.CGIdentifier;
import io.github.null2264.cobblegen.data.JanksonSerializable;
import io.github.null2264.cobblegen.data.Pair;
import io.github.null2264.cobblegen.data.model.PacketSerializable;
import lombok.val;
import net.minecraft.network.FriendlyByteBuf;

import java.util.Arrays;
import java.util.HashMap;

public class GeneratorMap extends HashMap<CGIdentifier, ResultList> implements JanksonSerializable, PacketSerializable<GeneratorMap> {
    public GeneratorMap() {}

    public GeneratorMap(int capacity) {
        super(capacity);
    }

    @SafeVarargs
    public static GeneratorMap of(Pair<CGIdentifier, ResultList>... gens) {
        val map = new GeneratorMap();
        Arrays.stream(gens).forEach(pair -> map.put(pair.getFirst(), pair.getSecond()));
        return map;
    }

    @Override
    @Serializer
    public JsonElement toJson() {
        JsonObject result = new JsonObject();
        this.forEach((k, v) -> result.put(k.toString(), v.toJson()));
        return result;
    }

    @Deserializer
    public static GeneratorMap fromJson(JsonObject json) {
        if (json == null) return null;

        GeneratorMap result = new GeneratorMap();
        json.forEach((k, v) -> {
            if (!(v instanceof JsonArray)) return;
            result.put(CGIdentifier.of(k), ResultList.fromJson(v));
        });
        return result;
    }

    static int capacity(int expectedSize) {
        if (expectedSize < 3) {
            if (expectedSize < 0) {
                throw new IllegalArgumentException("expectedSize cannot be negative but was: " + expectedSize);
            }
            return expectedSize + 1;
        }
        if (expectedSize < Ints.MAX_POWER_OF_TWO) {
            // This is the calculation used in JDK8 to resize when a putAll
            // happens; it seems to be the most conservative calculation we
            // can make.  0.75 is the default load factor.
            return (int) ((float) expectedSize / 0.75F + 1.0F);
        }
        return Integer.MAX_VALUE; // any large value
    }

    @Override
    public void toPacket(FriendlyByteBuf buf) {
        buf.writeMap(
                this,
                (o, key) -> key.writeToBuf(o), (o, blocks) -> o.writeCollection(blocks, (p, block) -> block.toPacket(p))
        );
    }

    public static GeneratorMap withExpectedSize(int expectedSize) {
        return new GeneratorMap(capacity(expectedSize));
    }

    public static GeneratorMap fromPacket(FriendlyByteBuf buf) {
        int i = buf.readVarInt();
        val map = GeneratorMap.withExpectedSize(i);

        for(int j = 0; j < i; ++j) {
            val id = CGIdentifier.readFromBuf(buf);
            val list = buf.readList(WeightedBlock::fromPacket);
            map.put(id, new ResultList(list));
        }

        return map;
    }
}