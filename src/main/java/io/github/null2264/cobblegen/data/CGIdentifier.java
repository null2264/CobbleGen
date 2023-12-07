package io.github.null2264.cobblegen.data;

import io.github.null2264.cobblegen.util.Util;
import lombok.val;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;

import static io.github.null2264.cobblegen.CobbleGen.MOD_ID;

/**
 * Replaces MC's ResourceLocation, in case MC's ResourceLocation changed
 * @param modid
 * @param name
 */
public record CGIdentifier(String modid, String name) {
    // TODO: Add validation
    public static CGIdentifier of(String id) {
        if (id.equals("*")) return wildcard();

        val split = id.split(":", 2);
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
        return new ResourceLocation(modid, name);
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
        return this.toString().replace('/', '_').replace(':', '_');
    }
}