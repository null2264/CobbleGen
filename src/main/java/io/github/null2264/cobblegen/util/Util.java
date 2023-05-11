package io.github.null2264.cobblegen.util;

import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static io.github.null2264.cobblegen.CobbleGen.MOD_ID;

public class Util
{
    public static Identifier identifierOf(GeneratorType type) {
        return new Identifier(MOD_ID, type.name().toLowerCase());
    }

    public static Identifier identifierOf(String id) {
        return new Identifier(MOD_ID, id);
    }

    @NotNull
    public static <T> T notNullOr(@Nullable T nullable, @NotNull T notNull) {
        if (nullable == null)
            return notNull;
        return nullable;
    }
}