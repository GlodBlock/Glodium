package com.glodblock.github.glodium.network.packet.sync;

import it.unimi.dsi.fastutil.objects.Object2ReferenceOpenHashMap;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.function.Consumer;

public final class ActionMap {

    private final Map<String, Consumer<Paras>> map = new Object2ReferenceOpenHashMap<>();

    private ActionMap() {
        // NO-OP
    }

    public static ActionMap create() {
        return new ActionMap();
    }

    public ActionMap put(String id, Consumer<Paras> pattern) {
        this.map.put(id, pattern);
        return this;
    }

    @Nullable
    public Consumer<Paras> get(String id) {
        return this.map.get(id);
    }

}
