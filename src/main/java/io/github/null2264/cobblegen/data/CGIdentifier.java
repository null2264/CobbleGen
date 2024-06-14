package io.github.null2264.cobblegen.data;

import blue.endless.jankson.JsonElement;
import blue.endless.jankson.JsonPrimitive;
import blue.endless.jankson.annotation.Deserializer;
import blue.endless.jankson.annotation.Serializer;
import io.github.null2264.cobblegen.util.Util;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;

import java.util.Objects;

import static io.github.null2264.cobblegen.CobbleGen.MOD_ID;
import static io.github.null2264.cobblegen.util.Util.identifierOf;

//#if MC<=1.16.5
//$$ public class CGIdentifier
//#else
/**
 * Replaces MC's ResourceLocation, in case MC's ResourceLocation changed
 * @param modid
 * @param name
 */
public record CGIdentifier(String modid, String name)
//#endif
{
    //#if MC<=1.16.5
    //$$ private final String modid;
    //$$ private final String name;

    //$$ public CGIdentifier(String modid, String name) {
    //$$     this.modid = modid;
    //$$     this.name = name;
    //$$ }

    //$$ public String modid() {
    //$$     return modid;
    //$$ }

    //$$ public String name() {
    //$$     return name;
    //$$ }
    //#endif

    // TODO: Add validation
    public static CGIdentifier of(String id) {
        if (id.equals("*")) return wildcard();

        String[] split = id.split(":", 2);
        if (split.length < 1)
            throw new RuntimeException("Invalid ID");
        if (split.length == 1)
            return new CGIdentifier(MOD_ID, split[0]);

        return new CGIdentifier(split[0], split[1]);
    }

    public static CGIdentifier wildcard() {
        return new CGIdentifier(MOD_ID, "*");
    }

    public boolean isWildcard() {
        return name.equals("*");
    }

    @Override
    public String toString() {
        if (isWildcard()) return "*";
        return String.format("%s:%s", modid, name);
    }

    public static CGIdentifier fromMC(ResourceLocation location) {
        return new CGIdentifier(location.getNamespace(), location.getPath());
    }

    public ResourceLocation toMC() {
        if (isWildcard()) throw new RuntimeException("Wildcard is not a valid MC ID");
        return identifierOf(modid, name);
    }

    public static CGIdentifier fromBlock(Block block) {
        return fromMC(Util.getBlockId(block));
    }

    public void writeToBuf(FriendlyByteBuf buf) {
        buf.writeUtf(this.toString());
    }

    public static CGIdentifier readFromBuf(FriendlyByteBuf buf) {
        return of(buf.readUtf());
    }

    public String toDebugFileName() {
        if (isWildcard()) return MOD_ID + "/wildcard";
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
    public boolean equals(Object obj) {
        if (!(obj instanceof CGIdentifier)) return false;
        return this.modid().equals(((CGIdentifier) obj).modid()) && this.name().equals(((CGIdentifier) obj).name());
    }

    @Override
    public int hashCode() {
        return Objects.hash(modid, name);
    }
}