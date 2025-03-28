package io.github.null2264.cobblegen.network.payload;

import net.minecraft.network.FriendlyByteBuf;

import java.util.function.Function;

@FunctionalInterface
public interface CGPayloadReader<T> extends Function<FriendlyByteBuf, T> {
}