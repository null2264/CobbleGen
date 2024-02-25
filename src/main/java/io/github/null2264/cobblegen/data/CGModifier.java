package io.github.null2264.cobblegen.data;

import io.github.null2264.cobblegen.compat.ByteBufCompat;
import net.minecraft.network.FriendlyByteBuf;

import java.util.List;
import java.util.Objects;

import static io.github.null2264.cobblegen.compat.CollectionCompat.streamToList;

/**
 * Class to holds modifier as Map key
 */
public class CGModifier {
    List<CGIdentifier> modifiers;

    public CGModifier(List<CGIdentifier> modifiers) {
        if (modifiers.size() >= 4) throw new IllegalArgumentException("Cannot have more than 4 modifiers");
        this.modifiers = streamToList(modifiers.stream().sorted());
    }

    public void writeToBuf(ByteBufCompat buf) {
        buf.writeCollection(modifiers, (b, o) -> o.writeToBuf(b));
    }

    public static CGModifier readFromBuf(ByteBufCompat buf) {
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