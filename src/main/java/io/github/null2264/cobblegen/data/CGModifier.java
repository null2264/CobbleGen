package io.github.null2264.cobblegen.data;

import net.minecraft.network.FriendlyByteBuf;

import java.util.List;

/**
 * Class to holds modifier as Map key
 */
public class CGModifier {
    List<CGIdentifier> modifiers;

    public CGModifier(List<CGIdentifier> modifiers) {
        this.modifiers = modifiers;
    }

    public void writeToBuf(FriendlyByteBuf buf) {
        buf.writeCollection(modifiers, (b, o) -> o.writeToBuf(b));
    }

    public static CGModifier readFromBuf(FriendlyByteBuf buf) {
        return new CGModifier(buf.readList(CGIdentifier::readFromBuf));
    }
}