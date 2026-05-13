package com.glodblock.github.glodium.registry.token;

import net.minecraft.world.level.block.entity.BlockEntityType;

import java.util.function.Supplier;

public record TileToken(Supplier<? extends BlockEntityType<?>> type, Class<?> token) {

}
