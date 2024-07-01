package com.glodblock.github.glodium.util;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.Pair;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;

public final class GlodCodecs {

    private GlodCodecs() {
        // NO-OP
    }

    public static final StreamCodec<RegistryFriendlyByteBuf, CompoundTag> NBT_STREAM_CODEC = StreamCodec.of(
            (buf, tag) -> buf.writeNbt(tag),
            buf -> Objects.requireNonNull(buf.readNbt())
    );

    public static <A, B> Codec<Pair<A, B>> pair(Codec<A> first, Codec<B> second) {
        return RecordCodecBuilder.create(
                builder -> builder
                        .group(
                                first.fieldOf("a").forGetter(Pair::left),
                                second.fieldOf("b").forGetter(Pair::right)
                        ).apply(builder, Pair::of)
        );
    }

    public static <K, V> Codec<Map<K, V>> map(Codec<K> key, Codec<V> value) {
        return map(key, value, HashMap::new);
    }

    public static <K, V> Codec<Map<K, V>> map(Codec<K> key, Codec<V> value, Supplier<? extends Map<K, V>> collector) {
        return Codec.list(pair(key, value))
                .xmap(list -> {
                    Map<K, V> map = collector.get();
                    list.forEach(p -> map.put(p.left(), p.right()));
                    return map;
                }, map -> {
                    List<Pair<K, V>> list = new ArrayList<>();
                    map.forEach((k, v) -> list.add(Pair.of(k, v)));
                    return list;
                });
    }

    public static <T> StreamCodec<RegistryFriendlyByteBuf, List<T>> list(StreamCodec<? super RegistryFriendlyByteBuf, T> codec) {
        return StreamCodec.of(
                (buf, l) -> {
                    buf.writeInt(l.size());
                    l.forEach(o -> codec.encode(buf, o));
                },
                buf -> {
                    List<T> list = new ArrayList<>();
                    int size = buf.readInt();
                    while (size > 0) {
                        list.add(codec.decode(buf));
                        size --;
                    }
                    return list;
                }
        );
    }

    public static <T> StreamCodec<RegistryFriendlyByteBuf, Optional<T>> optional(StreamCodec<? super RegistryFriendlyByteBuf, T> codec) {
        return StreamCodec.of(
                (buf, op) -> {
                    buf.writeBoolean(op.isPresent());
                    op.ifPresent(o -> codec.encode(buf, o));
                },
                buf -> {
                    if (buf.readBoolean()) {
                        return Optional.of(codec.decode(buf));
                    }
                    return Optional.empty();
                }
        );
    }

    public static <A, B> StreamCodec<RegistryFriendlyByteBuf, Pair<A, B>> pair(StreamCodec<? super RegistryFriendlyByteBuf, A> first, StreamCodec<? super RegistryFriendlyByteBuf, B> second) {
        return StreamCodec.composite(first, Pair::left, second, Pair::right, Pair::of);
    }

}
