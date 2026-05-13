package com.glodblock.github.glodium.registry.defer;

import net.minecraft.core.component.DataComponentType;
import net.minecraft.resources.ResourceKey;
import net.neoforged.neoforge.registries.DeferredHolder;
import org.jetbrains.annotations.NotNull;

public class DeferredDataComponentType<T> extends DeferredHolder<@NotNull DataComponentType<?>, @NotNull DataComponentType<@NotNull T>> {

    protected DeferredDataComponentType(ResourceKey<@NotNull DataComponentType<?>> key) {
        super(key);
    }

}
