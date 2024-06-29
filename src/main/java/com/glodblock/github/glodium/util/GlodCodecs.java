package com.glodblock.github.glodium.util;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public final class GlodCodecs {

    private GlodCodecs() {
        // NO-OP
    }

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

}
