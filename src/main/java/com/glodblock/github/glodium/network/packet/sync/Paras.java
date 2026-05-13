package com.glodblock.github.glodium.network.packet.sync;

import org.jetbrains.annotations.NotNull;

public record Paras(@NotNull Object[] paras) {

    public Paras(Object[] paras) {
        this.paras = paras == null ? new Object[0] : paras;
    }

    @SuppressWarnings("unchecked")
    public <T> T get(int index) {
        return (T) this.paras[index];
    }

    public <E extends Enum<E>> E getEnum(int index, E[] list) {
        return list[(int) this.get(index)];
    }

}
