package com.glodblock.github.glodium.registry.defer;

import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.jetbrains.annotations.NotNull;

public class DeferredDataComponentRegister extends DeferredRegister<@NotNull DataComponentType<?>> {

    public DeferredDataComponentRegister(String namespace) {
        super(BuiltInRegistries.DATA_COMPONENT_TYPE.key(), namespace);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Override
    protected <I extends DataComponentType<?>> @NotNull DeferredHolder<@NotNull DataComponentType<?>, @NotNull I> createHolder(@NotNull ResourceKey<? extends @NotNull Registry<@NotNull DataComponentType<?>>> registryKey, @NotNull Identifier key) {
        return new DeferredDataComponentType(ResourceKey.create(registryKey, key));
    }

}
