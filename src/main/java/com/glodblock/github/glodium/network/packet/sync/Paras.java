package com.glodblock.github.glodium.network.packet.sync;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class Paras {

    @NotNull
    private final Object[] paras;

    public Paras(Object[] objs) {
        this.paras = objs == null ? new Object[0] : objs;
    }

    @SuppressWarnings("unchecked")
    public <T> T get(int index) {
        return (T) this.paras[index];
    }

    @SuppressWarnings("unchecked")
    @Nullable
    public <T> T getSoft(int index) {
        if (index < 0 || index >= this.paras.length) {
            return null;
        }
        return (T) this.paras[index];
    }

    public int getParaAmount() {
        return this.paras.length;
    }

}
