package com.glodblock.github.glodium.util;

import it.unimi.dsi.fastutil.objects.Object2ReferenceMap;
import it.unimi.dsi.fastutil.objects.Object2ReferenceOpenCustomHashMap;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.fml.ModList;
import net.neoforged.fml.loading.LoadingModList;
import net.neoforged.fml.loading.moddiscovery.ModInfo;

public class GlodUtil {

    private static final Object2ReferenceMap<Class<?>, BlockEntityType<? extends BlockEntity>> TILE_CACHE = new Object2ReferenceOpenCustomHashMap<>(HashUtil.CLASS);

    @SuppressWarnings("all")
    public static <T extends BlockEntity> BlockEntityType<T> getTileType(Class<T> clazz, BlockEntityType.BlockEntitySupplier<? extends T> supplier, Block block) {
        if (block == null) {
            return (BlockEntityType<T>) TILE_CACHE.get(clazz);
        }
        return (BlockEntityType<T>) TILE_CACHE.computeIfAbsent(
                clazz,
                k -> BlockEntityType.Builder.of(supplier, block).build(null)
        );
    }

    @SuppressWarnings("unchecked")
    public static <T extends BlockEntity> BlockEntityType<T> getTileType(Class<T> clazz) {
        if (!TILE_CACHE.containsKey(clazz)) {
            throw new IllegalArgumentException(String.format("%s isn't an tile entity!", clazz.getName()));
        }
        return (BlockEntityType<T>) TILE_CACHE.get(clazz);
    }

    public static boolean checkInvalidRL(String rl, Registry<?> registry) {
        return checkInvalidRL(new ResourceLocation(rl), registry);
    }

    public static boolean checkInvalidRL(ResourceLocation rl, Registry<?> registry) {
        return registry.containsKey(rl);
    }

    public static double clamp(double num, double floor, double ceil) {
        return Math.min(ceil, Math.max(floor, num));
    }

    public static boolean checkMod(String modid) {
        if (ModList.get() == null) {
            return LoadingModList.get().getMods()
                    .stream().map(ModInfo::getModId)
                    .anyMatch(modid::equals);
        } else {
            return ModList.get().isLoaded(modid);
        }
    }

}
