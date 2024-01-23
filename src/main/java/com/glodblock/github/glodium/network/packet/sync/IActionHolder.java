package com.glodblock.github.glodium.network.packet.sync;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.function.Consumer;

public interface IActionHolder {

    @NotNull
    Map<String, Consumer<Paras>> getActionMap();

    default Map<String, Consumer<Paras>> createHolder() {
        return new Object2ObjectOpenHashMap<>();
    }

}
