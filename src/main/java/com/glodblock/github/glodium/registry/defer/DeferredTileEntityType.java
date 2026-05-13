package com.glodblock.github.glodium.registry.defer;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredHolder;
import org.jetbrains.annotations.NotNull;

public class DeferredTileEntityType<T extends BlockEntity> extends DeferredHolder<@NotNull BlockEntityType<?>, @NotNull BlockEntityType<@NotNull T>> {

    protected DeferredTileEntityType(ResourceKey<@NotNull BlockEntityType<?>> key) {
        super(key);
    }

}
