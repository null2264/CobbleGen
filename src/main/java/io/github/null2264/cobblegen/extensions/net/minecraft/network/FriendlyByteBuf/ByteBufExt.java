package io.github.null2264.cobblegen.extensions.net.minecraft.network.FriendlyByteBuf;

//#if MC<=1.16.5
//$$ import com.google.common.collect.Lists;
//$$ import com.google.common.collect.Maps;
//$$ import manifold.ext.rt.api.This;
//$$ import org.jetbrains.annotations.NotNull;
//$$
//$$ import java.util.Collection;
//$$ import java.util.List;
//$$ import java.util.Map;
//$$ import java.util.Optional;
//$$ import java.util.function.BiConsumer;
//$$ import java.util.function.Function;
//$$ import java.util.function.IntFunction;
//#endif

import io.netty.buffer.Unpooled;
import manifold.ext.rt.api.Extension;
import net.minecraft.network.FriendlyByteBuf;

@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
@Extension
public class ByteBufExt
{
    @Extension
    public static FriendlyByteBuf unpooled() {
        return new FriendlyByteBuf(Unpooled.buffer());
    }

    //#if MC<=1.16.5
    //$$ public static <T> void writeOptional(@This FriendlyByteBuf thiz, Optional<T> optional, @NotNull BiConsumer<FriendlyByteBuf, T> predicate) {
    //$$     if (optional.isPresent()) {
    //$$         thiz.writeBoolean(true);
    //$$         predicate.accept(thiz, optional.get());
    //$$     } else {
    //$$         thiz.writeBoolean(false);
    //$$     }
    //$$ }

    //$$ public static <T> @NotNull Optional<T> readOptional(@This FriendlyByteBuf thiz, @NotNull Function<FriendlyByteBuf, T> predicate) {
    //$$     return thiz.readBoolean() ? Optional.of(predicate.apply(thiz)) : Optional.empty();
    //$$ }

    //$$ public static <T> void writeCollection(@This FriendlyByteBuf thiz, Collection<T> collection, @NotNull BiConsumer<FriendlyByteBuf, T> predicate) {
    //$$     thiz.writeVarInt(collection.size());

    //$$     for(T object : collection) {
    //$$         predicate.accept(thiz, object);
    //$$     }
    //$$ }

    //$$ public static <T, C extends Collection<T>> @NotNull C readCollection(@This FriendlyByteBuf thiz, IntFunction<C> intPredicate, @NotNull Function<FriendlyByteBuf, T> predicate) {
    //$$     int i = thiz.readVarInt();
    //$$     C collection = intPredicate.apply(i);

    //$$     for(int j = 0; j < i; ++j) {
    //$$         collection.add(predicate.apply(thiz));
    //$$     }

    //$$     return collection;
    //$$ }

    //$$ public static <T> @NotNull List<T> readList(@This FriendlyByteBuf thiz, @NotNull Function<FriendlyByteBuf, T> predicate) {
    //$$     return thiz.readCollection(Lists::newArrayListWithCapacity, predicate);
    //$$ }

    //$$ public static <K, V> void writeMap(@This FriendlyByteBuf thiz, Map<K, V> map, @NotNull BiConsumer<FriendlyByteBuf, K> predicate, @NotNull BiConsumer<FriendlyByteBuf, V> predicate1) {
    //$$     thiz.writeVarInt(map.size());
    //$$     map.forEach((object, object2) -> {
    //$$         predicate.accept(thiz, object);
    //$$         predicate1.accept(thiz, object2);
    //$$     });
    //$$ }

    //$$ public static <K, V, M extends Map<K, V>> @NotNull M readMap(@This FriendlyByteBuf thiz, IntFunction<M> intPredicate, @NotNull Function<FriendlyByteBuf, K> predicate, @NotNull Function<FriendlyByteBuf, V> predicate1) {
    //$$     int i = thiz.readVarInt();
    //$$     M map = intPredicate.apply(i);

    //$$     for(int j = 0; j < i; ++j) {
    //$$         K object = predicate.apply(thiz);
    //$$         V object2 = predicate1.apply(thiz);
    //$$         map.put(object, object2);
    //$$     }

    //$$     return map;
    //$$ }

    //$$ public static <K, V> @NotNull Map<K, V> readMap(@This FriendlyByteBuf thiz, @NotNull Function<FriendlyByteBuf, K> predicate, @NotNull Function<FriendlyByteBuf, V> predicate2) {
    //$$     return thiz.readMap(Maps::newHashMapWithExpectedSize, predicate, predicate2);
    //$$ }
    //#endif
}