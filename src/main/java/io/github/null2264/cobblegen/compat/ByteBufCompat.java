package io.github.null2264.cobblegen.compat;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.FriendlyByteBuf;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.IntFunction;

public class ByteBufCompat extends FriendlyByteBuf {
    public ByteBufCompat(ByteBuf byteBuf) {
        super(byteBuf);
    }

    //#if MC<=1.16.5
    //$$ public <T> void writeOptional(Optional<T> optional, @NotNull BiConsumer<FriendlyByteBuf, T> predicate) {
    //$$     if (optional.isPresent()) {
    //$$         this.writeBoolean(true);
    //$$         predicate.accept(this, optional.get());
    //$$     } else {
    //$$         this.writeBoolean(false);
    //$$     }
    //$$ }

    //$$ public <T> @NotNull Optional<T> readOptional(@NotNull Function<FriendlyByteBuf, T> predicate) {
    //$$     return this.readBoolean() ? Optional.of(predicate.apply(this)) : Optional.empty();
    //$$ }

    //$$ public <T> void writeCollection(Collection<T> collection, @NotNull BiConsumer<FriendlyByteBuf, T> predicate) {
    //$$     this.writeVarInt(collection.size());

    //$$     for(T object : collection) {
    //$$         predicate.accept(this, object);
    //$$     }
    //$$ }

    //$$ public <T, C extends Collection<T>> @NotNull C readCollection(IntFunction<C> intPredicate, @NotNull Function<FriendlyByteBuf, T> predicate) {
    //$$     int i = this.readVarInt();
    //$$     C collection = intPredicate.apply(i);

    //$$     for(int j = 0; j < i; ++j) {
    //$$         collection.add(predicate.apply(this));
    //$$     }

    //$$     return collection;
    //$$ }

    //$$ public <T> @NotNull List<T> readList(@NotNull Function<FriendlyByteBuf, T> predicate) {
    //$$     return this.readCollection(Lists::newArrayListWithCapacity, predicate);
    //$$ }

    //$$ public <K, V> void writeMap(Map<K, V> map, @NotNull BiConsumer<FriendlyByteBuf, K> predicate, @NotNull BiConsumer<FriendlyByteBuf, V> predicate1) {
    //$$     this.writeVarInt(map.size());
    //$$     map.forEach((object, object2) -> {
    //$$         predicate.accept(this, object);
    //$$         predicate1.accept(this, object2);
    //$$     });
    //$$ }

    //$$ public <K, V, M extends Map<K, V>> @NotNull M readMap(IntFunction<M> intPredicate, @NotNull Function<FriendlyByteBuf, K> predicate, @NotNull Function<FriendlyByteBuf, V> predicate1) {
    //$$     int i = this.readVarInt();
    //$$     M map = intPredicate.apply(i);

    //$$     for(int j = 0; j < i; ++j) {
    //$$         K object = predicate.apply(this);
    //$$         V object2 = predicate1.apply(this);
    //$$         map.put(object, object2);
    //$$     }

    //$$     return map;
    //$$ }

    //$$ public <K, V> @NotNull Map<K, V> readMap(@NotNull Function<FriendlyByteBuf, K> predicate, @NotNull Function<FriendlyByteBuf, V> predicate2) {
    //$$     return this.readMap(Maps::newHashMapWithExpectedSize, predicate, predicate2);
    //$$ }
    //#endif
}