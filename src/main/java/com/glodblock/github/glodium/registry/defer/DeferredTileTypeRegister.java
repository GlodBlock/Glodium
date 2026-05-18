package com.glodblock.github.glodium.registry.defer;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.jetbrains.annotations.NotNull;

public class DeferredTileTypeRegister extends DeferredRegister<@NotNull BlockEntityType<?>> {

    public DeferredTileTypeRegister(String namespace) {
        super(BuiltInRegistries.BLOCK_ENTITY_TYPE.key(), namespace);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Override
    protected <I extends @NotNull BlockEntityType<?>> @NotNull DeferredHolder<@NotNull BlockEntityType<?>, I> createHolder(@NotNull ResourceKey<? extends @NotNull Registry<@NotNull BlockEntityType<?>>> registryKey, @NotNull Identifier key) {
        return new DeferredTileEntityType(ResourceKey.create(registryKey, key));
    }

}
