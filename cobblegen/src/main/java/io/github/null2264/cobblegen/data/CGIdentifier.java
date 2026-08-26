package io.github.null2264.cobblegen.data;

import blue.endless.jankson.JsonElement;
import blue.endless.jankson.JsonPrimitive;
import blue.endless.jankson.annotation.Deserializer;
import blue.endless.jankson.annotation.Serializer;
import io.github.null2264.cobblegen.util.GeneratorType;
import io.github.null2264.cobblegen.util.Util;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

import static io.github.null2264.cobblegen.CobbleGen.MOD_ID;

/**
 * Replaces MC's ResourceLocation, in case MC's ResourceLocation changed
 * @param modid
 * @param name
 */
public final class CGIdentifier
{
    private final String modid;
    private final String name;

    public CGIdentifier(String modid, String name) {
        this.modid = modid;
        this.name = name;
    }

    public String modid() {
        return modid;
    }

    public String name() {
        return name;
    }

    public static CGIdentifier of(String id) {
        if (id.equals("*")) return wildcard();

        String @NotNull[] split = id.split(":", 2);
        String modId;
        String name;
        if (split.length < 1) {
            throw new RuntimeException("Invalid ID");
        } else if (split.length == 1) {
            modId = MOD_ID;
            name = split[0];
        } else {
            modId = split[0];
            name = split[1];
        }

        if (!isValidPart(modId, false)) {
            throw new RuntimeException("Invalid mod id!");
        }

        if (!isValidPart(name, true)) {
            throw new RuntimeException("Invalid name!");
        }

        return new CGIdentifier(modId, name);
    }

    public static CGIdentifier of(GeneratorType type) {
        return of(type.name().toLowerCase());
    }

    private static boolean isValidPart(String s, boolean isName) {
        if (s.isEmpty()) return false;
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (isName && (c == '*' || c == '/')) continue;

            boolean isGood = (c == '_' || c == '-' || c == '.' || (c >= 'a' && c <= 'z') || (c >= '0' && c <= '9'));
            if (!isGood) return false;
        }
        return true;
    }

    public static CGIdentifier wildcard() {
        return new CGIdentifier(MOD_ID, "*");
    }

    public boolean isWildcard() {
        return name.equals("*");
    }

    @Override
    public @NotNull String toString() {
        if (isWildcard()) return "*";
        return String.format("%s:%s", modid, name);
    }

    public static CGIdentifier fromMC(
        @Nullable
        net.minecraft.resources.
        #if MC>=12111
        Identifier
        #else
        ResourceLocation
        #endif
        location
    ) {
        if (location == null) return null;
        String modId = location.getNamespace();
        String name = location.getPath();
        if (name.equals(MOD_ID + "/wildcard")) {
            name = "*";
        }
        return new CGIdentifier(modId, name);
    }

    public net.minecraft.resources.
    #if MC>=12111
    Identifier
    #else
    ResourceLocation
    #endif
    toMC() {
        String actualName = isWildcard() ? (MOD_ID + "/wildcard") : name();
        #if MC>=12100
        return net.minecraft.resources.
            #if MC>=12111
            Identifier
            #else
            ResourceLocation
            #endif
            .fromNamespaceAndPath(modid(), actualName);
        #else
        return new net.minecraft.resources.ResourceLocation(modid(), actualName);
        #endif
    }

    public static CGIdentifier fromBlock(Block block) {
        return Util.getBlockId(block);
    }

    public void writeToBuf(FriendlyByteBuf buf) {
        buf.writeUtf(this.toString());
    }

    public static CGIdentifier readFromBuf(FriendlyByteBuf buf) {
        return of(buf.readUtf());
    }

    public String toDebugFileName() {
        if (isWildcard()) return modid() + "_" + MOD_ID + "_wildcard";
        return this.toString().replace('/', '_').replace(':', '_');
    }

    @Serializer
    public JsonElement toJson() {
        return JsonPrimitive.of(toString());
    }

    @Deserializer
    public static CGIdentifier fromJson(JsonPrimitive json) {
        return of(json.asString());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CGIdentifier id = (CGIdentifier) o;
        return this.modid().equals(id.modid()) && this.name().equals(id.name());
    }

    @Override
    public int hashCode() {
        return Objects.hash(modid, name);
    }
}
