package io.github.null2264.cobblegen.data;

import net.minecraft.network.FriendlyByteBuf;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Class to holds modifier as Map key
 */
public class CGModifier {
    List<CGIdentifier> modifiers;

    public CGModifier(List<CGIdentifier> modifiers) {

        this.modifiers = modifiers.stream().sorted().toList();
    }

    public void writeToBuf(FriendlyByteBuf buf) {
        buf.writeCollection(modifiers, (b, o) -> o.writeToBuf(b));
    }

    public static CGModifier readFromBuf(FriendlyByteBuf buf) {
        return new CGModifier(buf.readList(CGIdentifier::readFromBuf));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CGModifier that = (CGModifier) o;
        return Objects.equals(modifiers, that.modifiers);
    }

    @Override
    public int hashCode() {
        return Objects.hash(modifiers);
    }
}